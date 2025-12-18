package com.contentria.api.post.dto

import java.time.ZonedDateTime
import java.util.UUID

data class PostPartInResponse(
    val id: UUID,
    val slug: String,
    val title: String,
    val summary: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val featuredImageUrl: String?,
    val publishedAt: ZonedDateTime?,
    val categoryName: String?
) {
    companion object {
        fun from(postDetailInfo: PostDetailInfo): PostPartInResponse {
            return PostPartInResponse(
                id = postDetailInfo.id,
                slug = postDetailInfo.slug,
                title = postDetailInfo.title,
                summary = postDetailInfo.contentMarkdown.take(100),
                metaTitle = postDetailInfo.metaTitle,
                metaDescription = postDetailInfo.metaDescription,
                featuredImageUrl = postDetailInfo.featuredImageUrl,
                publishedAt = postDetailInfo.publishedAt,
                categoryName = postDetailInfo.categoryName
            )
        }
    }
}
