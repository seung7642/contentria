package com.contentria.api.post.controller.dto

import com.contentria.api.post.application.dto.PostContentInfo
import com.contentria.api.post.application.dto.PostDetailInfo
import com.contentria.api.user.application.dto.UserInfo
import java.time.ZonedDateTime
import java.util.*

data class PostDetailResponse(
    val post: PostResponse,
    val author: AuthorResponse,
    val blogSlug: String,
    val categoryName: String?
) {
    data class PostResponse(
        val id: UUID,
        val slug: String,
        val title: String,
        val contentMarkdown: String,
        val metaTitle: String?,
        val metaDescription: String?,
        val featuredImageUrl: String?,
        val publishedAt: ZonedDateTime?
    ) {
        companion object {
            fun from(post: PostContentInfo): PostResponse {
                return PostResponse(
                    id = post.id,
                    slug = post.slug,
                    title = post.title,
                    contentMarkdown = post.contentMarkdown,
                    metaTitle = post.metaTitle,
                    metaDescription = post.metaDescription,
                    featuredImageUrl = post.featuredImageUrl,
                    publishedAt = post.publishedAt,
                )
            }
        }
    }

    data class AuthorResponse(
        val userId: UUID,
        val username: String,
        val profileImageUrl: String?
    ) {
        companion object {
            fun from(info: UserInfo): AuthorResponse {
                return AuthorResponse(
                    userId = info.userId,
                    username = info.name,
                    profileImageUrl = info.pictureUrl
                )
            }
        }
    }

    companion object {
        fun from(postDetailInfo: PostDetailInfo): PostDetailResponse {
            return PostDetailResponse(
                post = PostResponse.from(postDetailInfo.post),
                author = AuthorResponse.from(postDetailInfo.author),
                blogSlug = postDetailInfo.blogSlug,
                categoryName = postDetailInfo.categoryName
            )
        }
    }
}
