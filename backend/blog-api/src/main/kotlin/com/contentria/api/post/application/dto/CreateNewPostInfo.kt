package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostStatus
import java.time.ZonedDateTime
import java.util.UUID

data class CreateNewPostInfo(
    val postId: UUID,
    val slug: String,
    val title: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val publishedAt: ZonedDateTime?,
    val status: PostStatus,
    val categoryId: UUID?
) {
    companion object {
        fun from(post: Post): CreateNewPostInfo {
            return CreateNewPostInfo(
                postId = post.id!!,
                slug = post.slug,
                title = post.title,
                metaTitle = post.metaTitle,
                metaDescription = post.metaDescription,
                publishedAt = post.publishedAt,
                status = post.status,
                categoryId = post.categoryId
            )
        }
    }
}
