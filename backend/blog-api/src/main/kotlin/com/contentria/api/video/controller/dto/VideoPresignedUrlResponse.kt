package com.contentria.api.video.controller.dto

import com.contentria.api.video.application.dto.VideoPresignedUrlInfo
import java.util.*

data class VideoPresignedUrlResponse(
    val presignedUrl: String,
    val videoId: UUID
) {
    companion object {
        fun from(info: VideoPresignedUrlInfo): VideoPresignedUrlResponse {
            return VideoPresignedUrlResponse(
                presignedUrl = info.presignedUrl,
                videoId = info.videoId
            )
        }
    }
}
