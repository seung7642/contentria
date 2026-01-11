package com.contentria.api.post.controller.dto

import com.contentria.api.post.application.dto.PostDetailInfo
import com.contentria.api.user.controller.dto.UserSummaryResponse
import java.time.ZonedDateTime
import java.util.UUID

data class PostDetailResponse(
    val post: Post,
    val owner: UserSummaryResponse
) {
    companion object {
        fun from(postDetailInfo: PostDetailInfo): PostDetailResponse {
            return PostDetailResponse(
                post = Post.from(postDetailInfo.post),
                owner = UserSummaryResponse.from(postDetailInfo.owner)
            )
        }
    }

    data class Post(
        val id: UUID,
        val slug: String,
        val title: String,
        val contentMarkdown: String,
        val metaTitle: String?,
        val metaDescription: String?,
        val featuredImageUrl: String?,
        val publishedAt: ZonedDateTime?,
        val categoryName: String?
    ) {
        companion object {
            fun from(post: PostDetailInfo.Post): Post {
                return Post(
                    id = post.id,
                    slug = post.slug,
                    title = post.title,
                    contentMarkdown = post.contentMarkdown,
                    metaTitle = post.metaTitle,
                    metaDescription = post.metaDescription,
                    featuredImageUrl = post.featuredImageUrl,
                    publishedAt = post.publishedAt,
                    categoryName = post.categoryName
                )
            }
        }
    }
}
