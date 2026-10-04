package com.contentria.api.media.controller.dto

import com.contentria.api.media.application.dto.PresignedUrlInfo
import java.util.*

data class PresignedUrlResponse(
    val presignedUrl: String,
    val publicUrl: String,
    val mediaId: UUID
) {
    companion object {
        fun from(info: PresignedUrlInfo): PresignedUrlResponse {
            return PresignedUrlResponse(
                presignedUrl = info.presignedUrl,
                publicUrl = info.publicUrl,
                mediaId = info.mediaId
            )
        }
    }
}
