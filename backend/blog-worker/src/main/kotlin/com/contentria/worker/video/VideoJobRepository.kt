package com.contentria.worker.video

import com.contentria.worker.transcode.TranscodeResult
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Access to the `videos` table via JdbcTemplate (mirrors blog-batch's table access; no
 * dependency on blog-api's JPA entity). Only the columns the worker needs are touched.
 */
@Repository
class VideoJobRepository(
    private val jdbcTemplate: JdbcTemplate,
) {

    fun findByRawKey(rawKey: String): VideoJob? {
        return jdbcTemplate.query(
            "SELECT id, status FROM videos WHERE raw_key = ?",
            { rs, _ -> VideoJob(UUID.fromString(rs.getString("id")), rs.getString("status")) },
            rawKey,
        ).firstOrNull()
    }

    fun markProcessing(videoId: UUID) {
        jdbcTemplate.update(
            "UPDATE videos SET status = 'PROCESSING', updated_at = now() WHERE id = ?",
            videoId,
        )
    }

    fun markCompleted(videoId: UUID, result: TranscodeResult) {
        jdbcTemplate.update(
            """
            UPDATE videos
                SET status = 'COMPLETED',
                    hls_prefix = ?,
                    master_key = ?,
                    poster_key = ?,
                    duration_ms = ?,
                    width = ?,
                    height = ?,
                    updated_at = now()
            WHERE id = ?
            """.trimIndent(),
            result.hlsPrefix,
            result.masterKey,
            result.posterKey,
            result.durationMs,
            result.width,
            result.height,
            videoId,
        )
    }

    fun markFailed(videoId: UUID, errorMessage: String) {
        jdbcTemplate.update(
            "UPDATE videos SET status = 'FAILED', error_message = ?, updated_at = now() WHERE id = ?",
            errorMessage,
            videoId,
        )
    }
}

data class VideoJob(
    val id: UUID,
    val status: String,
)
