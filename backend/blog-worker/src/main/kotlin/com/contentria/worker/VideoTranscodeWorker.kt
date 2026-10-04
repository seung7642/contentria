package com.contentria.worker

import com.contentria.worker.queue.CloudflareQueueClient
import com.contentria.worker.queue.QueueMessage
import com.contentria.worker.storage.WorkerStorageClient
import com.contentria.worker.transcode.PermanentTranscodeException
import com.contentria.worker.transcode.TranscodeJob
import com.contentria.worker.transcode.Transcoder
import com.contentria.worker.video.VideoJobRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

private val log = KotlinLogging.logger {}

/**
 * Polls the Cloudflare Queue and drives each message through the transcode lifecycle.
 *
 * Ack semantics:
 *  - success or permanently-handled (no row, already terminal, irrelevant action) → ack.
 *  - transient failure (exception) → do NOT ack; the queue redelivers after the
 *    visibility timeout, and after max retries the message lands in the DLQ.
 */
@Component
class VideoTranscodeWorker(
    private val queueClient: CloudflareQueueClient,
    private val videoJobRepository: VideoJobRepository,
    private val transcoder: Transcoder,
    private val storageClient: WorkerStorageClient,
) {

    @Scheduled(fixedDelayString = "\${worker.poll.fixed-delay-ms:5000}")
    fun poll() {
        val messages = try {
            queueClient.pull()
        } catch (e: Exception) {
            log.error(e) { "Queue pull failed; will retry next tick" }
            return
        }

        for (message in messages) {
            try {
                handle(message)
                queueClient.ack(listOf(message.leaseId))
            } catch (e: Exception) {
                log.error(e) {
                    "Transient failure for leaseId=${message.leaseId}, key=${message.objectKey}; " +
                        "leaving unacked for redelivery"
                }
            }
        }
    }

    /**
     * Processes one message. Contract: returning normally means the message is handled
     * (success, permanent failure, or a deliberate skip) and must be acked; a thrown
     * exception means a transient failure — the caller leaves it unacked for redelivery.
     */
    private fun handle(message: QueueMessage) {
        if (message.action != ACTION_PUT_OBJECT && message.action != ACTION_COMPLETE_MULTIPART) {
            log.debug { "Ignoring action=${message.action} key=${message.objectKey}" }
            return
        }

        val job = videoJobRepository.findByRawKey(message.objectKey)
        if (job == null) {
            log.warn { "No videos row for key=${message.objectKey}; acking" }
            return
        }
        if (job.status == STATUS_COMPLETED || job.status == STATUS_DELETED) {
            log.info { "Video ${job.id} already ${job.status}; acking (idempotent)" }
            return
        }

        videoJobRepository.markProcessing(job.id)
        try {
            val result = transcoder.transcode(TranscodeJob(job.id, message.objectKey))
            videoJobRepository.markCompleted(job.id, result)
            log.info { "Video ${job.id} marked COMPLETED" }
            // After COMPLETED the raw source is no longer needed. Best-effort delete —
            // the raw/ Lifecycle backstop covers any miss. Done after markCompleted so a
            // redelivery (status COMPLETED) is acked+skipped without needing the raw.
            deleteRawQuietly(message.objectKey, job.id)
        } catch (e: PermanentTranscodeException) {
            // Bad input — retrying won't help. Mark FAILED (reader shows "처리 실패") and ack.
            log.warn { "Permanent transcode failure for video ${job.id}: ${e.message}" }
            videoJobRepository.markFailed(job.id, e.message ?: "transcode failed")
        }
        // Transient failures propagate to poll() → left unacked → redelivery → DLQ.
    }

    private fun deleteRawQuietly(rawKey: String, videoId: UUID) {
        try {
            storageClient.delete(rawKey)
        } catch (e: Exception) {
            log.warn(e) { "Failed to delete raw source for video $videoId (key=$rawKey); Lifecycle backstop will handle it" }
        }
    }

    companion object {
        private const val ACTION_PUT_OBJECT = "PutObject"
        private const val ACTION_COMPLETE_MULTIPART = "CompleteMultipartUpload"
        private const val STATUS_COMPLETED = "COMPLETED"
        private const val STATUS_DELETED = "DELETED"
    }
}
