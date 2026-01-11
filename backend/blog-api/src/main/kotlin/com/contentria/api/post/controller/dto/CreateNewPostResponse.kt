package com.contentria.api.post.controller.dto

import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.application.dto.CreateNewPostInfo
import java.time.ZonedDateTime
import java.util.UUID

data class CreateNewPostResponse(
    val postId: UUID,
    val slug: String,
    val title: String,
    val metaTitle: String?,
    val metaDescription: String?,
    val publishedAt: ZonedDateTime?,
    val status: PostStatus,
    val categoryName: String?
) {
    companion object {
        fun from(info: CreateNewPostInfo): CreateNewPostResponse {
            return CreateNewPostResponse(
                postId = info.postId,
                slug = info.slug,
                title = info.title,
                metaTitle = info.metaTitle,
                metaDescription = info.metaDescription,
                publishedAt = info.publishedAt,
                status = info.status,
                categoryName = info.categoryName
            )
        }
    }
}
