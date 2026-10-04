package com.contentria.api.media.infrastructure

import com.contentria.api.media.domain.Media
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.*

@Repository
interface MediaJpaRepository : JpaRepository<Media, UUID> {

    fun findByPublicUrlIn(publicUrls: List<String>): List<Media>
    fun findByPostId(postId: UUID): List<Media>

    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM Media m WHERE m.uploaderId = :uploaderId AND m.createdAt > :since")
    fun sumFileSizeByUploaderIdAndCreatedAtAfter(uploaderId: UUID, since: ZonedDateTime): Long

    fun countByPostId(postId: UUID): Int
}
