package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostStatus
import java.time.ZonedDateTime
import java.util.*

data class PostContentInfo(
    val id: UUID,
    val slug: String,
    val title: String,
    val contentMarkdown: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val featuredImageUrl: String?,
    val status: PostStatus,
    val likeCount: Int,
    val viewCount: Int,
    val publishedAt: ZonedDateTime?,
    val categoryId: UUID?,
    val blogId: UUID
) {
    companion object {
        fun from(post: Post): PostContentInfo {
            return PostContentInfo(
                id = post.id!!,
                slug = post.slug,
                title = post.title,
                contentMarkdown = post.contentMarkdown,
                metaTitle = post.metaTitle,
                metaDescription = post.metaDescription,
                featuredImageUrl = post.featuredImageUrl,
                status = post.status,
                likeCount = post.likeCount,
                viewCount = post.viewCount,
                publishedAt = post.publishedAt,
                categoryId = post.categoryId,
                blogId = post.blogId
            )
        }
    }
}
