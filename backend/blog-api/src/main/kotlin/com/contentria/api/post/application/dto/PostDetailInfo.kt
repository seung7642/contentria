package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.domain.query.PostDetailView
import com.contentria.api.user.application.dto.UserSummaryInfo
import java.time.ZonedDateTime
import java.util.UUID

data class PostDetailInfo(
    val post: Post,
    val owner: UserSummaryInfo
) {
    companion object {
        fun from(view: PostDetailView): PostDetailInfo {
            return PostDetailInfo(
                post = Post.from(view),
                owner = UserSummaryInfo(
                    username = view.writerName,
                    pictureUrl = view.writerProfileUrl
                )
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
            fun from(view: PostDetailView): Post {
                return Post(
                    id = view.id,
                    slug = view.slug,
                    title = view.title,
                    contentMarkdown = view.contentMarkdown,
                    metaTitle = view.metaTitle,
                    metaDescription = view.metaDescription,
                    featuredImageUrl = view.featuredImageUrl,
                    status = view.status,
                    likeCount = view.likeCount,
                    viewCount = view.viewCount,
                    publishedAt = view.publishedAt,
                    categoryName = view.categoryName,
                    blogSlug = view.blogSlug
                )
            }
        }
    }
}