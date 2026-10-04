package com.contentria.api.post.application.dto

import com.contentria.api.post.domain.query.RecentPublishedPost
import java.time.ZonedDateTime
import java.util.UUID

data class RecentPostInfo(
    val postId: UUID,
    val title: String,
    val summary: String,
    val postSlug: String,
    val blogSlug: String,
    val authorNickname: String,
    val publishedAt: ZonedDateTime?
) {
    companion object {
        fun from(post: RecentPublishedPost): RecentPostInfo {
            return RecentPostInfo(
                postId = post.postId,
                title = post.title,
                summary = post.summary,
                postSlug = post.postSlug,
                blogSlug = post.blogSlug,
                authorNickname = post.authorNickname,
                publishedAt = post.publishedAt
            )
        }
    }
}
