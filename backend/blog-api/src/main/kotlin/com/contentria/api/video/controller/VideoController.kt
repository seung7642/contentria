package com.contentria.api.video.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.video.application.VideoService
import com.contentria.api.video.controller.dto.VideoPresignedUrlRequest
import com.contentria.api.video.controller.dto.VideoPresignedUrlResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/videos")
class VideoController(
    private val videoService: VideoService
) {

    @PostMapping("/presigned-url")
    fun createPresignedUrl(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @Valid @RequestBody request: VideoPresignedUrlRequest
    ): ResponseEntity<VideoPresignedUrlResponse> {
        log.debug { "Requesting video presigned URL: userId=${userDetails.userId}, fileName=${request.fileName}" }
        val info = videoService.createPresignedUrl(userDetails.userId, request.toCommand())
        return ResponseEntity.ok(VideoPresignedUrlResponse.from(info))
    }
}
