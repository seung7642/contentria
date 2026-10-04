package com.contentria.api.video.application.dto

data class VideoPresignedUrlCommand(
    val fileName: String,
    val contentType: String,
    val fileSize: Long
)
