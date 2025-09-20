package com.contentria.api.blog.dto

import java.time.ZonedDateTime
import java.util.UUID

data class PostSummaryDto(
    val id: UUID,
    val slug: String,
    val title: String,
    val excerpt: String,
    val featuredImageUrl: String?,
    val publishedAt: ZonedDateTime,
    val categoryName: String?,
    val likeCount: Int
)
