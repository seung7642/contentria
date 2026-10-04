package com.contentria.api.media.controller.dto

import com.contentria.api.media.application.dto.PresignedUrlCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class PresignedUrlRequest(
    @field:NotBlank(message = "File name is required.")
    val fileName: String,

    @field:NotBlank(message = "Content type is required.")
    val contentType: String,

    @field:Positive(message = "File size must be positive.")
    val fileSize: Long
) {
    fun toCommand(): PresignedUrlCommand {
        return PresignedUrlCommand(
            fileName = this.fileName,
            contentType = this.contentType,
            fileSize = this.fileSize
        )
    }
}
