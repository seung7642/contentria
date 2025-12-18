package com.contentria.api.post.dto

import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostStatus
import java.time.ZonedDateTime
import java.util.UUID

// 서비스 계층의 '조회'를 담당하는 메서드의 입력 객체의 네이밍은 접미사로 'Info' 를 사용한다.
data class PostDetailInfo(
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
        fun from(post: Post): PostDetailInfo {
            return PostDetailInfo(
                id = post.id!!,
                slug = post.slug,
                title = post.title,
                contentMarkdown = post.contentMarkdown,
                metaTitle = post.metaTitle,
                metaDescription = post.metaDescription,
                featuredImageUrl = post.featuredImageUrl,
                status = post.status,
                likeCount = post.likeCount,
                viewCount = post.viewCount,
                publishedAt = post.publishedAt,
                categoryName = post.category?.name,
                blogSlug = post.blog.slug
            )
        }
    }
}