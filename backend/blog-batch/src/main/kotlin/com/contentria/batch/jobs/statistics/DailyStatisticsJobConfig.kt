package com.contentria.batch.jobs.statistics

import com.contentria.common.domain.analytics.DailyStatistics
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.database.JdbcCursorItemReader
import org.springframework.batch.item.database.JpaItemWriter
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate
import javax.sql.DataSource

@Configuration
class DailyStatisticsJobConfig(
    private val dataSource: DataSource,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun dailyStatisticsJob(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .start(dailyStatisticsStep(jobRepository, transactionManager))
            .build()
    }

    /**
     * Configures the Step for calculating daily statistics.
     *
     * **Key Implementation Details:**
     * - **[JobScope]**: Ensures a new instance of this Step is created for every
     *   Job execution. This allows the Step to access run-specific context (like JobParameters).
     * - **Why pass `null`?**:
     *   The reader and processor are **[StepScope]** beans that utilize Late Binding.
     *   At startup, Spring creates **proxies** for them, ignoring the arguments passed here.
     *   The actual values (e.g., via SpEL `@Value`) are injected dynamically by the framework
     *   at runtime, making the `null` values mere placeholders to satisfy the compiler.
     *
     *   @return The configured Step instance.
     */
    @JobScope
    @Bean
    fun dailyStatisticsStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<DailyStatisticsDto, DailyStatistics>(CHUNK_SIZE, transactionManager)
            .reader(dailyStatisticsReader(null)) // @StepScope로 인해 런타임에 주입됨
            .processor(dailyStatisticsProcessor(null))
            .writer(dailyStatisticsWriter())
            .build()
    }

    /**
     * Reads aggregated visit statistics (PV & UV) from the database using raw SQL.
     *
     * **Why [JdbcCursorItemReader]?**
     * - **Performance**: It streams data directly via a database cursor, avoiding the overhead of JPA (Hibernate)
     *   entity mapping and dirty checking. This is ideal for reading large datasets.
     * - **Flexibility**: Easier to execute complex aggregation queries (like `GROUPING SETS`) which are difficult to express in JPQL/QueryDSL.
     *
     * **[StepScope] Explained:**
     * - Marks this bean to be initialized **only when the step actually starts**, not at application startup.
     * - This "Late Binding" is necessary to inject runtime parameters (like `targetDate` from jobParameters) via SpEL.
     */
    @StepScope
    @Bean
    fun dailyStatisticsReader(
        @Value("#{jobParameters[targetDate]}") targetDateStr: String?
    ): JdbcCursorItemReader<DailyStatisticsDto> {
        val targetDate = targetDateStr?.let { LocalDate.parse(it) } ?: LocalDate.now().minusDays(1)

        val startDateTime = targetDate.atStartOfDay()
        val endDateTime = targetDate.plusDays(1).atStartOfDay()

        val sql = """
            SELECT
                blog_id,
                post_id,
                COUNT(DISTINCT visitor_ip) AS visit_count, -- UV 계산
                COUNT(*) AS view_count -- PV 계산
            FROM contentria.visit_logs
            WHERE visited_at >= ? AND visited_at < ?
            GROUP BY GROUPING SETS (
                (blog_id, post_id),  -- 포스트별 통계
                (blog_id)            -- 블로그 전체 통계
            )
        """.trimIndent()

        return JdbcCursorItemReaderBuilder<DailyStatisticsDto>()
            .name("dailyStatisticsReader")
            .dataSource(dataSource)
            .sql(sql)
            .queryArguments(startDateTime, endDateTime)
            .rowMapper(DataClassRowMapper(DailyStatisticsDto::class.java))
            .build()
    }

    /**
     * Transforms raw aggregated data ([DailyStatisticsDto]) into the domain entity ([DailyStatistics])
     *
     * **Responsibility:**
     * - Maps the DTO read from the database to the JPA entity to be saved.
     * - Applies business logic, such as setting the statistical reference date (`statDate`).
     *
     * **[StepScope] Usage:**
     * - Required here because the logic depends on `targetDateStr`, which is provided dynamically
     *   via [JobParameters] at runtime (`${jobParameters[...]}`).
     */
    @StepScope
    @Bean
    fun dailyStatisticsProcessor(
        @Value("#{jobParameters[targetDate]}") targetDateStr: String?
    ): ItemProcessor<DailyStatisticsDto, DailyStatistics> {
        val targetDate = targetDateStr?.let { LocalDate.parse(it) } ?: LocalDate.now().minusDays(1)

        return ItemProcessor { item ->
            DailyStatistics(
                blogId = item.blogId,
                postId = item.postId,
                statDate = targetDate,
                visitCount = item.visitCount,
                viewCount = item.viewCount
            )
        }
    }

    @Bean
    fun dailyStatisticsWriter(): JpaItemWriter<DailyStatistics> {
        return JpaItemWriterBuilder<DailyStatistics>()
            .entityManagerFactory(entityManagerFactory)
            .build()
    }

    companion object {
        const val JOB_NAME = "dailyStatisticsJob"
        const val STEP_NAME = "dailyStatisticsStep"
        const val CHUNK_SIZE = 1000
    }
}