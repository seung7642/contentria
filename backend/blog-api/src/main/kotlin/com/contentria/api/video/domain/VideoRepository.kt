package com.contentria.api.video.domain

import java.util.*

interface VideoRepository {

    fun findById(id: UUID): Video?
    /** The active (non-DELETED) video attached to a post, if any. */
    fun findActiveByPostId(postId: UUID): Video?
    fun findByRawKey(rawKey: String): Video?
    fun save(video: Video): Video
    fun delete(video: Video)
}
