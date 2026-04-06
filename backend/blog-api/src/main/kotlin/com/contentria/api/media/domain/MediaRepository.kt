package com.contentria.api.media.domain

import java.util.*

interface MediaRepository {

    fun findById(id: UUID): Media?
    fun save(media: Media): Media
    fun delete(media: Media)
    fun deleteAll(media: List<Media>)
    fun findByPublicUrlIn(publicUrls: List<String>): List<Media>
    fun findByPostId(postId: UUID): List<Media>
}
