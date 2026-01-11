package com.contentria.api.post.domain.query

import com.contentria.api.post.domain.PostStatus
import java.time.ZonedDateTime
import java.util.UUID

data class PostDetailView(
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
    val publishedAt: ZonedDateTime,

    val blogSlug: String,
    val categoryName: String?,

    val writerId: UUID,
    val writerName: String,
    val writerEmail: String,
    val writerProfileUrl: String?
)
