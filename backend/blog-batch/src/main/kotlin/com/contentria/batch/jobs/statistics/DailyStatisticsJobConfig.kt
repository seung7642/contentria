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

    @Bean
    @JobScope
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

    @Bean
    @StepScope
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

    @Bean
    @StepScope
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