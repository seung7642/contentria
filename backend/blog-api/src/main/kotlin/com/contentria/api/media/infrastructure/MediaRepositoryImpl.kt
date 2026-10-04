package com.contentria.api.media.infrastructure

import com.contentria.api.media.domain.Media
import com.contentria.api.media.domain.MediaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.*

@Repository
class MediaRepositoryImpl(
    private val mediaJpaRepository: MediaJpaRepository
) : MediaRepository {

    override fun findById(id: UUID): Media? {
        return mediaJpaRepository.findByIdOrNull(id)
    }

    override fun save(media: Media): Media {
        return mediaJpaRepository.save(media)
    }

    override fun delete(media: Media) {
        mediaJpaRepository.delete(media)
    }

    override fun deleteAll(media: List<Media>) {
        mediaJpaRepository.deleteAll(media)
    }

    override fun findByPublicUrlIn(publicUrls: List<String>): List<Media> {
        return mediaJpaRepository.findByPublicUrlIn(publicUrls)
    }

    override fun findByPostId(postId: UUID): List<Media> {
        return mediaJpaRepository.findByPostId(postId)
    }

    override fun sumFileSizeByUploaderIdAndCreatedAtAfter(uploaderId: UUID, since: ZonedDateTime): Long {
        return mediaJpaRepository.sumFileSizeByUploaderIdAndCreatedAtAfter(uploaderId, since)
    }

    override fun countByPostId(postId: UUID): Int {
        return mediaJpaRepository.countByPostId(postId)
    }
}
