package com.contentria.api.media.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.media.application.MediaService
import com.contentria.api.media.controller.dto.PresignedUrlRequest
import com.contentria.api.media.controller.dto.PresignedUrlResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/media")
class MediaController(
    private val mediaService: MediaService
) {

    @PostMapping("/presigned-url")
    fun createPresignedUrl(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @Valid @RequestBody request: PresignedUrlRequest
    ): ResponseEntity<PresignedUrlResponse> {
        log.debug { "Requesting presigned URL: userId=${userDetails.userId}, fileName=${request.fileName}" }
        val info = mediaService.createPresignedUrl(userDetails.userId, request.toCommand())
        return ResponseEntity.ok(PresignedUrlResponse.from(info))
    }

    @DeleteMapping("/{mediaId}")
    fun deleteMedia(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable mediaId: UUID
    ): ResponseEntity<Void> {
        log.debug { "Deleting media: mediaId=$mediaId, userId=${userDetails.userId}" }
        mediaService.deleteMedia(userDetails.userId, mediaId)
        return ResponseEntity.noContent().build()
    }
}
