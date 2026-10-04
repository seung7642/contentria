package com.contentria.worker.transcode

import java.util.UUID

/**
 * Port for turning a raw uploaded video into HLS outputs. Implemented by [FfmpegTranscoder].
 *
 * A [PermanentTranscodeException] signals a bad input (not a video, too long, …) that must
 * not be retried — the worker marks the row FAILED and acks. Any other exception is treated
 * as transient (left unacked → redelivery → DLQ).
 */
interface Transcoder {
    fun transcode(job: TranscodeJob): TranscodeResult
}

data class TranscodeJob(
    val videoId: UUID,
    val rawKey: String,
)

data class TranscodeResult(
    val hlsPrefix: String,
    val masterKey: String,
    val posterKey: String?,
    val durationMs: Long?,
    val width: Int?,
    val height: Int?,
)

/** Bad input that should not be retried (e.g. not a video, exceeds the max duration). */
class PermanentTranscodeException(message: String) : RuntimeException(message)
