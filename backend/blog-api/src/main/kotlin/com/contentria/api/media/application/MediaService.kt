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
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class MediaService(
    private val mediaRepository: MediaRepository,
    private val r2StorageClient: R2StorageClient,
    private val mediaValidator: MediaValidator,
    private val appProperties: AppProperties
) {

    private val markdownImagePattern = Regex("""!\[.*?]\((.*?)\)""")

    @Transactional
    fun createPresignedUrl(userId: UUID, command: PresignedUrlCommand): PresignedUrlInfo {
        validateFileType(command.contentType)
        validateFileSize(command.fileSize)
        validateDailyUploadQuota(userId, command.fileSize)

        val extension = extractExtension(command.fileName)
        // Upload to the temporary prefix. Objects are promoted to the permanent prefix
        // when the post is saved (see promoteTemporaryMedia). Orphans in tmp/ are
        // automatically removed by the R2 Object Lifecycle Rule (24h expiry).
        val storedKey = "$TMP_PREFIX/$userId/${UUID.randomUUID()}.$extension"
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

    /**
     * Promotes all temporary media referenced in the markdown to the permanent prefix.
     *
     * For each image URL in the markdown whose media record still lives under `tmp/`,
     * the object is copied from `tmp/` to `media/` in R2, the original `tmp/` object
     * is deleted, and the media record's storedKey/publicUrl are updated. The markdown
     * is rewritten so that references point to the new permanent URLs.
     *
     * Returns the rewritten markdown. If no temporary media is found, the original
     * markdown is returned unchanged.
     */
    @Transactional
    fun promoteTemporaryMedia(markdown: String): String {
        val imageUrls = extractImageUrls(markdown)
        if (imageUrls.isEmpty()) return markdown

        val mediaList = mediaRepository.findByPublicUrlIn(imageUrls)
        val temporaryMedia = mediaList.filter { it.storedKey.startsWith("$TMP_PREFIX/") }
        if (temporaryMedia.isEmpty()) return markdown

        val urlReplacements = mutableMapOf<String, String>()
        for (media in temporaryMedia) {
            validateMediaContent(media)

            val oldKey = media.storedKey
            val newKey = oldKey.replaceFirst("$TMP_PREFIX/", "$MEDIA_PREFIX/")
            val newPublicUrl = "${appProperties.r2.publicUrl}/$newKey"

            r2StorageClient.copyObject(oldKey, newKey)
            r2StorageClient.deleteObject(oldKey)

            val oldPublicUrl = media.publicUrl
            media.storedKey = newKey
            media.publicUrl = newPublicUrl

            urlReplacements[oldPublicUrl] = newPublicUrl
        }

        log.info { "Promoted ${temporaryMedia.size} temporary media to permanent storage" }

        return urlReplacements.entries.fold(markdown) { acc, (old, new) -> acc.replace(old, new) }
    }

    /**
     * Synchronizes media-post links for a saved post.
     *
     * - Links all media referenced in the markdown to the given postId (idempotent).
     * - For media previously linked to this postId but no longer referenced, unlinks
     *   them and deletes the underlying R2 object and DB record.
     *
     * This must be called after [promoteTemporaryMedia] so that the markdown contains
     * only permanent (`media/`) URLs.
     */
    @Transactional
    fun syncMediaForPost(postId: UUID, markdown: String) {
        val currentImageUrls = extractImageUrls(markdown)
        validatePostImageLimit(currentImageUrls.size)

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
            val mediaToDelete = previousMedia.filter { it.publicUrl in removedUrls }
            mediaToDelete.forEach { r2StorageClient.deleteObject(it.storedKey) }
            mediaRepository.deleteAll(mediaToDelete)
            log.info { "Deleted ${mediaToDelete.size} unlinked media from postId=$postId" }
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

    private fun validateDailyUploadQuota(userId: UUID, fileSize: Long) {
        val startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
        val usedBytes = mediaRepository.sumFileSizeByUploaderIdAndCreatedAtAfter(userId, startOfDay)
        if (usedBytes + fileSize > appProperties.r2.dailyUploadLimitBytes) {
            log.warn { "Daily upload quota exceeded: userId=$userId, usedBytes=$usedBytes, requestedBytes=$fileSize" }
            throw ContentriaException(ErrorCode.MEDIA_DAILY_UPLOAD_QUOTA_EXCEEDED)
        }
    }

    private fun validateMediaContent(media: Media) {
        val headerBytes = r2StorageClient.getObjectHeadBytes(
            media.storedKey, MediaValidator.HEADER_BYTES_NEEDED
        )

        val isValid = if (media.contentType == "image/webp") {
            MediaValidator.isValidWebP(headerBytes)
        } else {
            mediaValidator.validateMagicNumber(headerBytes, media.contentType)
        }

        if (!isValid) {
            log.warn {
                "Content-type mismatch: mediaId=${media.id}, declared=${media.contentType}, " +
                    "header=${headerBytes.take(12).joinToString(" ") { "%02X".format(it) }}"
            }
            r2StorageClient.deleteObject(media.storedKey)
            mediaRepository.delete(media)
            throw ContentriaException(ErrorCode.MEDIA_CONTENT_TYPE_MISMATCH)
        }
    }

    private fun validatePostImageLimit(imageCount: Int) {
        if (imageCount > appProperties.r2.maxImagesPerPost) {
            throw ContentriaException(ErrorCode.MEDIA_POST_IMAGE_LIMIT_EXCEEDED)
        }
    }

    private fun extractExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "jpg").lowercase()
    }

    companion object {
        const val TMP_PREFIX = "tmp"
        const val MEDIA_PREFIX = "media"

        val ALLOWED_CONTENT_TYPES = setOf(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
        )
    }
}
