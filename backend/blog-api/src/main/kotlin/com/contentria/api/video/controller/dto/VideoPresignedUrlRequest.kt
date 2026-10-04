package com.contentria.api.video.controller.dto

import com.contentria.api.video.application.dto.VideoPresignedUrlCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class VideoPresignedUrlRequest(
    @field:NotBlank(message = "File name is required.")
    val fileName: String,

    @field:NotBlank(message = "Content type is required.")
    val contentType: String,

    @field:Positive(message = "File size must be positive.")
    val fileSize: Long
) {
    fun toCommand(): VideoPresignedUrlCommand {
        return VideoPresignedUrlCommand(
            fileName = this.fileName,
            contentType = this.contentType,
            fileSize = this.fileSize
        )
    }
}
