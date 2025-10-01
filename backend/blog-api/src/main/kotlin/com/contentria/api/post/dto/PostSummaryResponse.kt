package com.contentria.api.post.dto

import java.time.ZonedDateTime
import java.util.UUID

data class PostSummaryResponse(
    val id: UUID,
    val slug: String,
    val title: String,
    val summary: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val featuredImageUrl: String?,
    val publishedAt: ZonedDateTime,
    val likeCount: Int,
    val viewCount: Int,
    val categoryName: String?
) {
    companion object {
        fun from(info: PostSummaryInfo): PostSummaryResponse {
            return PostSummaryResponse(
                id = info.id,
                slug = info.slug,
                title = info.title,
                summary = info.summary,
                metaTitle = info.metaTitle,
                metaDescription = info.metaDescription,
                featuredImageUrl = info.featuredImageUrl,
                publishedAt = info.publishedAt,
                likeCount = info.likeCount,
                viewCount = info.viewCount,
                categoryName = info.categoryName
            )
        }
    }
}
