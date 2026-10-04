package com.contentria.api.media.application.dto

import java.util.*

data class PresignedUrlInfo(
    val presignedUrl: String,
    val publicUrl: String,
    val mediaId: UUID
)
