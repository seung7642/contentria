package com.contentria.batch.jobs.statistics

import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter
import org.springframework.batch.infrastructure.item.database.JdbcCursorItemReader
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder
import org.springframework.batch.infrastructure.item.database.builder.JdbcCursorItemReaderBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import org.springframework.transaction.PlatformTransactionManager
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import javax.sql.DataSource

@Configuration
class DailyStatisticsJobConfig(
    private val dataSource: DataSource
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

    @JobScope
    @Bean
    fun dailyStatisticsStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .chunk<DailyStatisticsDto, DailyStatisticsWriteItem>(CHUNK_SIZE, transactionManager)
            .reader(dailyStatisticsReader(null))
            .processor(dailyStatisticsProcessor(null))
            .writer(dailyStatisticsWriter())
            .build()
    }

    /**
     * Reads aggregated visit statistics (PV & UV) for the target day.
     *
     * Bounds are computed as [startOfDay, nextStartOfDay) in Asia/Seoul, so the window
     * stays correct regardless of the pod's system timezone. visit_logs.visited_at is a
     * timestamptz, and binding Timestamp values derived from ZonedDateTime (converted
     * to Instant) avoids JDBC session-TZ conversion ambiguity.
     */
    @StepScope
    @Bean
    fun dailyStatisticsReader(
        @Value("#{jobParameters[targetDate]}") targetDateStr: String?
    ): JdbcCursorItemReader<DailyStatisticsDto> {
        val targetDate = parseTargetDate(targetDateStr)

        val startTs = Timestamp.from(targetDate.atStartOfDay(REPORTING_ZONE).toInstant())
        val endTs = Timestamp.from(targetDate.plusDays(1).atStartOfDay(REPORTING_ZONE).toInstant())

        val sql = """
            SELECT
                blog_id,
                post_id,
                COUNT(DISTINCT visitor_ip) AS visit_count,
                COUNT(*) AS view_count
            FROM contentria.visit_logs
            WHERE visited_at >= ? AND visited_at < ?
            GROUP BY GROUPING SETS (
                (blog_id, post_id),
                (blog_id)
            )
        """.trimIndent()

        return JdbcCursorItemReaderBuilder<DailyStatisticsDto>()
            .name("dailyStatisticsReader")
            .dataSource(dataSource)
            .sql(sql)
            .queryArguments(startTs, endTs)
            .rowMapper(DataClassRowMapper(DailyStatisticsDto::class.java))
            .build()
    }

    @StepScope
    @Bean
    fun dailyStatisticsProcessor(
        @Value("#{jobParameters[targetDate]}") targetDateStr: String?
    ): ItemProcessor<DailyStatisticsDto, DailyStatisticsWriteItem> {
        val targetDate = parseTargetDate(targetDateStr)
        return ItemProcessor { dto ->
            DailyStatisticsWriteItem(
                blogId = dto.blogId,
                postId = dto.postId,
                statDate = targetDate,
                visitCount = dto.visitCount,
                viewCount = dto.viewCount
            )
        }
    }

    /**
     * Upserts aggregated rows so the job is idempotent on re-run. The conflict target
     * (blog_id, post_id, stat_date) relies on uq_daily_stats_blog_post_date being
     * declared NULLS NOT DISTINCT, which lets the blog-wide aggregation row
     * (post_id IS NULL) participate in conflict resolution.
     */
    @Bean
    fun dailyStatisticsWriter(): JdbcBatchItemWriter<DailyStatisticsWriteItem> {
        val sql = """
            INSERT INTO contentria.daily_statistics
                (id, blog_id, post_id, stat_date, visit_count, view_count, created_at, updated_at)
            VALUES
                (gen_random_uuid(), :blogId, :postId, :statDate, :visitCount, :viewCount,
                 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            ON CONFLICT ON CONSTRAINT uq_daily_stats_blog_post_date DO UPDATE SET
                visit_count = EXCLUDED.visit_count,
                view_count = EXCLUDED.view_count,
                updated_at = CURRENT_TIMESTAMP
        """.trimIndent()

        return JdbcBatchItemWriterBuilder<DailyStatisticsWriteItem>()
            .dataSource(dataSource)
            .sql(sql)
            .itemSqlParameterSourceProvider { item -> item.toParameterSource() }
            .assertUpdates(false)
            .build()
    }

    private fun parseTargetDate(targetDateStr: String?): LocalDate =
        targetDateStr?.let { LocalDate.parse(it) } ?: LocalDate.now(REPORTING_ZONE).minusDays(1)

    companion object {
        const val JOB_NAME = "dailyStatisticsJob"
        const val STEP_NAME = "dailyStatisticsStep"
        const val CHUNK_SIZE = 1000
        private val REPORTING_ZONE: ZoneId = ZoneId.of("Asia/Seoul")
    }
}

data class DailyStatisticsWriteItem(
    val blogId: java.util.UUID,
    val postId: java.util.UUID?,
    val statDate: LocalDate,
    val visitCount: Long,
    val viewCount: Long
) {
    fun toParameterSource(): SqlParameterSource =
        MapSqlParameterSource()
            .addValue("blogId", blogId)
            .addValue("postId", postId)
            .addValue("statDate", statDate)
            .addValue("visitCount", visitCount)
            .addValue("viewCount", viewCount)
}
