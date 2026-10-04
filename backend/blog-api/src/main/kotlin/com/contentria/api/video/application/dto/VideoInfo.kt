package com.contentria.api.video.application.dto

import java.util.*

/**
 * The video attached to a post, resolved for the reader. Playback URLs are built
 * server-side and are non-null only once the video is COMPLETED.
 */
data class VideoInfo(
    val videoId: UUID,
    val status: String,
    val masterUrl: String?,
    val posterUrl: String?,
    val durationMs: Long?,
    val width: Int?,
    val height: Int?,
)
