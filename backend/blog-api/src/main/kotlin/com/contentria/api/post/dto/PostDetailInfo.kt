package com.contentria.api.post.dto

import com.contentria.api.post.domain.PostStatus
import com.contentria.api.user.dto.UserSummaryInfo
import java.time.ZonedDateTime
import java.util.UUID
import com.contentria.api.post.domain.Post as PostEntity

data class PostDetailInfo(
    val post: Post,
    val owner: UserSummaryInfo
) {
    companion object {
        fun from(postEntity: PostEntity): PostDetailInfo {
            return PostDetailInfo(
                post = Post.from(postEntity),
                owner = UserSummaryInfo.from(postEntity.blog.user)
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
        val status: PostStatus,
        val likeCount: Int,
        val viewCount: Int,
        val publishedAt: ZonedDateTime?,
        val categoryName: String?,
        val blogSlug: String
    ) {
        companion object {
            fun from(postEntity: PostEntity): Post {
                return Post(
                    id = postEntity.id!!,
                    slug = postEntity.slug,
                    title = postEntity.title,
                    contentMarkdown = postEntity.contentMarkdown,
                    metaTitle = postEntity.metaTitle,
                    metaDescription = postEntity.metaDescription,
                    featuredImageUrl = postEntity.featuredImageUrl,
                    status = postEntity.status,
                    likeCount = postEntity.likeCount,
                    viewCount = postEntity.viewCount,
                    publishedAt = postEntity.publishedAt,
                    categoryName = postEntity.category?.name,
                    blogSlug = postEntity.blog.slug
                )
            }
        }
    }
}