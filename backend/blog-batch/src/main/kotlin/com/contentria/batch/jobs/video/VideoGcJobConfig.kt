package com.contentria.batch.jobs.video

import com.contentria.batch.storage.VideoStorageClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager
import java.util.UUID

private val log = KotlinLogging.logger {}

/**
 * Periodic video cleanup (run hourly by a CronJob), in three parts:
 *  1. Reaper — rows stuck in PROCESSING past a threshold (e.g. DLQ'd) → FAILED.
 *  2. Abandoned uploads — never attached to a post → delete R2 objects + row.
 *  3. DELETED videos — replaced/removed → hard-delete after a short grace.
 *
 * `hls/` outputs are not covered by any R2 Lifecycle, so they must be deleted here.
 * FAILED rows are intentionally NOT time-GC'd (they are user-facing).
 */
@Configuration
class VideoGcJobConfig(
    private val jdbcTemplate: JdbcTemplate,
    private val videoStorageClient: VideoStorageClient,
    // Must exceed the max retry window (3 × 30 min visibility ≈ 90 min) so an in-flight retry isn't failed.
    @Value("\${video-gc.stuck-processing-minutes:120}") private val stuckProcessingMinutes: Long,
    @Value("\${video-gc.orphan-retention-days:7}") private val orphanRetentionDays: Long,
    @Value("\${video-gc.deleted-grace-minutes:60}") private val deletedGraceMinutes: Long,
) {

    @Bean
    fun videoGcJob(jobRepository: JobRepository, videoGcStep: Step): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .start(videoGcStep)
            .build()
    }

    @Bean
    fun videoGcStep(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .tasklet({ contribution, _ ->
                val reaped = reapStuckProcessing()
                val purgedOrphans = purge(findAbandonedUploads(), "abandoned")
                val purgedDeleted = purge(findDeletedVideos(), "deleted")

                val total = reaped + purgedOrphans + purgedDeleted
                contribution.incrementWriteCount(total.toLong())
                log.info {
                    "Video GC done: reaped(stuck PROCESSING)=$reaped, purged(abandoned)=$purgedOrphans, " +
                        "purged(deleted)=$purgedDeleted"
                }
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    private fun reapStuckProcessing(): Int {
        return jdbcTemplate.update(
            """
            UPDATE videos
               SET status = 'FAILED',
                   error_message = 'Transcode timed out (stuck in PROCESSING)',
                   updated_at = now()
             WHERE status = 'PROCESSING'
               AND updated_at < now() - (? * interval '1 minute')
            """.trimIndent(),
            stuckProcessingMinutes,
        )
    }

    private fun findAbandonedUploads(): List<VideoGcTarget> {
        return jdbcTemplate.query(
            """
            SELECT id, raw_key, hls_prefix FROM videos
             WHERE post_id IS NULL
               AND status <> 'DELETED'
               AND created_at < now() - (? * interval '1 day')
            """.trimIndent(),
            rowMapper,
            orphanRetentionDays,
        )
    }

    private fun findDeletedVideos(): List<VideoGcTarget> {
        return jdbcTemplate.query(
            """
            SELECT id, raw_key, hls_prefix FROM videos
             WHERE status = 'DELETED'
               AND updated_at < now() - (? * interval '1 minute')
            """.trimIndent(),
            rowMapper,
            deletedGraceMinutes,
        )
    }

    /** Deletes each target's R2 objects (raw + hls prefix), then the row. Best-effort on R2. */
    private fun purge(targets: List<VideoGcTarget>, label: String): Int {
        var count = 0
        for (target in targets) {
            try {
                videoStorageClient.delete(target.rawKey)
                target.hlsPrefix?.let { videoStorageClient.deletePrefix(it) }
                jdbcTemplate.update("DELETE FROM videos WHERE id = ?", target.id)
                count++
            } catch (e: Exception) {
                // Leave the row for the next run rather than orphaning R2 objects from a deleted row.
                log.warn(e) { "Failed to purge $label video ${target.id}; will retry next run" }
            }
        }
        return count
    }

    companion object {
        const val JOB_NAME = "videoGcJob"
        const val STEP_NAME = "videoGcStep"

        private val rowMapper = { rs: java.sql.ResultSet, _: Int ->
            VideoGcTarget(
                id = UUID.fromString(rs.getString("id")),
                rawKey = rs.getString("raw_key"),
                hlsPrefix = rs.getString("hls_prefix"),
            )
        }
    }
}

data class VideoGcTarget(
    val id: UUID,
    val rawKey: String,
    val hlsPrefix: String?,
)
