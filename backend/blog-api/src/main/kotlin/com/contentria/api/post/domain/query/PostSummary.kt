package com.contentria.api.post.domain.query

import com.contentria.api.post.domain.PostStatus
import java.time.ZonedDateTime
import java.util.UUID

data class PostSummary(
    val id: UUID,
    val slug: String,
    val title: String,
    val summary: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val status: PostStatus,
    val featuredImageUrl: String?,
    val publishedAt: ZonedDateTime?,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val likeCount: Int,
    val viewCount: Int,
    val categoryId: UUID?,
    val categoryName: String?
)