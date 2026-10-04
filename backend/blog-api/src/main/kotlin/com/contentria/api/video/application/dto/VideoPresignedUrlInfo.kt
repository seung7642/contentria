package com.contentria.api.video.application.dto

import java.util.*

data class VideoPresignedUrlInfo(
    val presignedUrl: String,
    val videoId: UUID
)
