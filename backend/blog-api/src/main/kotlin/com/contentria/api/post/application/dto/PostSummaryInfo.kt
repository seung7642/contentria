package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.domain.query.PostSummary
import java.time.ZonedDateTime
import java.util.UUID

data class PostSummaryInfo(
    val id: UUID,
    val slug: String,
    val title: String,
    val summary: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val status: PostStatus,
    val featuredImageUrl: String?,
    val publishedAt: ZonedDateTime,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val likeCount: Int,
    val viewCount: Int,
    val categoryName: String?
) {
    companion object {
        fun from(projection: PostSummary): PostSummaryInfo {
            return PostSummaryInfo(
                id = projection.id,
                slug = projection.slug,
                title = projection.title,
                summary = projection.summary,
                metaTitle = projection.metaTitle,
                metaDescription = projection.metaDescription,
                status = projection.status,
                featuredImageUrl = projection.featuredImageUrl,
                publishedAt = projection.publishedAt,
                createdAt = projection.createdAt,
                updatedAt = projection.updatedAt,
                likeCount = projection.likeCount,
                viewCount = projection.viewCount,
                categoryName = projection.categoryName
            )
        }
    }
}
