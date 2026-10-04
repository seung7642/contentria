package com.contentria.api.video.application

import com.contentria.api.global.properties.AppProperties
import com.contentria.api.video.application.dto.VideoInfo
import com.contentria.api.video.application.dto.VideoPresignedUrlCommand
import com.contentria.api.video.application.dto.VideoPresignedUrlInfo
import com.contentria.api.video.domain.Video
import com.contentria.api.video.domain.VideoRepository
import com.contentria.api.video.infrastructure.VideoStorageClient
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class VideoService(
    private val videoRepository: VideoRepository,
    private val videoStorageClient: VideoStorageClient,
    private val appProperties: AppProperties
) {

    /**
     * Creates a `PENDING` video record and returns a single presigned PUT URL for the
     * raw source upload. The frontend gets `videoId` immediately so it can attach the
     * video to the post; the upload itself goes browser → R2 directly.
     *
     * The raw object is later transcoded by blog-worker (the work queue is Cloudflare
     * Queue, which finds this row by `raw_key`). Real content validation (ffprobe) and
     * transcoding are out of scope here.
     */
    @Transactional
    fun createPresignedUrl(userId: UUID, command: VideoPresignedUrlCommand): VideoPresignedUrlInfo {
        validateContentType(command.contentType)
        validateFileSize(command.fileSize)

        val extension = extractExtension(command.fileName)
        // Per-upload prefix under raw/ enables recursive delete + the raw/ Lifecycle backstop.
        val rawKey = "$RAW_PREFIX/${UUID.randomUUID()}/original.$extension"

        val video = Video(
            uploaderId = userId,
            originalName = command.fileName,
            rawKey = rawKey,
            contentType = command.contentType,
            fileSize = command.fileSize
        )
        val savedVideo = videoRepository.save(video)

        val presignedUrl = videoStorageClient.generatePresignedPutUrl(rawKey, command.contentType, command.fileSize)

        log.info { "Video presigned URL created: videoId=${savedVideo.id}, userId=$userId" }

        return VideoPresignedUrlInfo(
            presignedUrl = presignedUrl,
            videoId = savedVideo.id!!
        )
    }

    /**
     * Links the chosen video to a post on save (called by PostFacade). One active video per
     * post: any previously-attached video is marked DELETED (GC reclaims it). `videoId == null`
     * removes the current video.
     */
    @Transactional
    fun syncPostVideo(postId: UUID, videoId: UUID?, uploaderId: UUID) {
        val current = videoRepository.findActiveByPostId(postId)

        if (videoId == null) {
            current?.markDeleted()
            return
        }
        if (current != null && current.id == videoId) {
            return
        }
        current?.markDeleted()

        val video = videoRepository.findById(videoId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_VIDEO)
        if (!video.isUploader(uploaderId)) {
            throw ContentriaException(ErrorCode.VIDEO_LINK_FORBIDDEN)
        }
        video.linkToPost(postId)

        log.info { "Linked video $videoId to post $postId" }
    }

    @Transactional(readOnly = true)
    fun getActiveVideoByPostId(postId: UUID): VideoInfo? {
        val video = videoRepository.findActiveByPostId(postId) ?: return null
        val publicUrl = appProperties.r2.publicUrl
        return VideoInfo(
            videoId = video.id!!,
            status = video.status.name,
            masterUrl = video.masterKey?.let { "$publicUrl/$it" },
            posterUrl = video.posterKey?.let { "$publicUrl/$it" },
            durationMs = video.durationMs,
            width = video.width,
            height = video.height,
        )
    }

    private fun validateContentType(contentType: String) {
        if (contentType !in appProperties.video.allowedContentTypes) {
            throw ContentriaException(ErrorCode.VIDEO_UNSUPPORTED_TYPE)
        }
    }

    private fun validateFileSize(fileSize: Long) {
        if (fileSize > appProperties.video.maxFileSizeBytes) {
            throw ContentriaException(ErrorCode.VIDEO_FILE_TOO_LARGE)
        }
    }

    private fun extractExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "mp4").lowercase()
    }

    companion object {
        const val RAW_PREFIX = "raw"
    }
}
