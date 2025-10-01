package com.contentria.api.post.dto

import com.contentria.api.post.repository.PostSummaryProjection
import java.time.ZonedDateTime
import java.util.UUID

data class PostSummaryInfo(
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
        fun from(projection: PostSummaryProjection): PostSummaryInfo {
            return PostSummaryInfo(
                id = projection.id,
                slug = projection.slug,
                title = projection.title,
                summary = projection.summary,
                metaTitle = projection.metaTitle,
                metaDescription = projection.metaDescription,
                featuredImageUrl = projection.featuredImageUrl,
                publishedAt = projection.publishedAt,
                likeCount = projection.likeCount,
                viewCount = projection.viewCount,
                categoryName = projection.categoryName
            )
        }
    }
}
