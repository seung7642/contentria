package com.contentria.api.media.infrastructure

import com.contentria.api.media.domain.Media
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MediaJpaRepository : JpaRepository<Media, UUID> {

    fun findByPublicUrlIn(publicUrls: List<String>): List<Media>
    fun findByPostId(postId: UUID): List<Media>
}
