package com.contentria.api.media.application

import com.contentria.api.global.properties.AppProperties
import com.contentria.api.media.application.dto.PresignedUrlCommand
import com.contentria.api.media.application.dto.PresignedUrlInfo
import com.contentria.api.media.domain.Media
import com.contentria.api.media.domain.MediaRepository
import com.contentria.api.media.infrastructure.R2StorageClient
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class MediaService(
    private val mediaRepository: MediaRepository,
    private val r2StorageClient: R2StorageClient,
    private val appProperties: AppProperties
) {

    private val markdownImagePattern = Regex("""!\[.*?]\((.*?)\)""")

    @Transactional
    fun createPresignedUrl(userId: UUID, command: PresignedUrlCommand): PresignedUrlInfo {
        validateFileType(command.contentType)
        validateFileSize(command.fileSize)

        val extension = extractExtension(command.fileName)
        val storedKey = "media/$userId/${UUID.randomUUID()}.$extension"
        val publicUrl = "${appProperties.r2.publicUrl}/$storedKey"

        val media = Media(
            uploaderId = userId,
            originalName = command.fileName,
            storedKey = storedKey,
            publicUrl = publicUrl,
            contentType = command.contentType,
            fileSize = command.fileSize
        )
        val savedMedia = mediaRepository.save(media)

        val presignedUrl = r2StorageClient.generatePresignedPutUrl(storedKey, command.contentType, command.fileSize)

        log.info { "Presigned URL created: mediaId=${savedMedia.id}, userId=$userId" }

        return PresignedUrlInfo(
            presignedUrl = presignedUrl,
            publicUrl = publicUrl,
            mediaId = savedMedia.id!!
        )
    }

    @Transactional
    fun deleteMedia(userId: UUID, mediaId: UUID) {
        val media = mediaRepository.findById(mediaId)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_MEDIA)

        if (!media.isUploader(userId)) {
            throw ContentriaException(ErrorCode.MEDIA_DELETE_FORBIDDEN)
        }

        r2StorageClient.deleteObject(media.storedKey)
        mediaRepository.delete(media)

        log.info { "Media deleted: mediaId=$mediaId, userId=$userId" }
    }

    @Transactional
    fun syncMediaForPost(postId: UUID, markdown: String) {
        val currentImageUrls = extractImageUrls(markdown)
        val previousMedia = mediaRepository.findByPostId(postId)
        val previousUrls = previousMedia.map { it.publicUrl }

        val newUrls = currentImageUrls.filter { it !in previousUrls }
        val removedUrls = previousUrls.filter { it !in currentImageUrls }

        if (newUrls.isNotEmpty()) {
            val mediaToLink = mediaRepository.findByPublicUrlIn(newUrls)
            mediaToLink.forEach { it.linkToPost(postId) }
            log.debug { "Linked ${mediaToLink.size} media to postId=$postId" }
        }

        if (removedUrls.isNotEmpty()) {
            val mediaToUnlink = mediaRepository.findByPublicUrlIn(removedUrls)
            mediaToUnlink.forEach { it.postId = null }
            log.debug { "Unlinked ${mediaToUnlink.size} media from postId=$postId" }
        }
    }

    private fun extractImageUrls(markdown: String): List<String> {
        val cdnBaseUrl = appProperties.r2.publicUrl
        return markdownImagePattern.findAll(markdown)
            .map { it.groupValues[1] }
            .filter { it.startsWith(cdnBaseUrl) }
            .toList()
    }

    private fun validateFileType(contentType: String) {
        if (contentType !in ALLOWED_CONTENT_TYPES) {
            throw ContentriaException(ErrorCode.UNSUPPORTED_MEDIA_TYPE)
        }
    }

    private fun validateFileSize(fileSize: Long) {
        if (fileSize > appProperties.r2.maxFileSizeBytes) {
            throw ContentriaException(ErrorCode.MEDIA_FILE_TOO_LARGE)
        }
    }

    private fun extractExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "jpg").lowercase()
    }

    companion object {
        val ALLOWED_CONTENT_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
        )
    }
}
