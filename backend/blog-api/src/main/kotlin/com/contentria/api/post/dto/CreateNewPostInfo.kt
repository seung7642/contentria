package com.contentria.api.post.dto

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
    val categoryName: String?
)
