package com.contentria.api.media.application.dto

data class PresignedUrlCommand(
    val fileName: String,
    val contentType: String,
    val fileSize: Long
)
