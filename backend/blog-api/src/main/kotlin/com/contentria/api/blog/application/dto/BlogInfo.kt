package com.contentria.api.blog.application.dto

import com.contentria.api.blog.domain.Blog
import java.time.ZonedDateTime
import java.util.UUID

data class BlogInfo(
    val id: UUID,
    val slug: String,
    val title: String,
    val description: String,
    val createdAt: ZonedDateTime,
    val userId: UUID
) {
    companion object {
        fun from(blog: Blog): BlogInfo {
            return BlogInfo(
                id = blog.id!!,
                slug = blog.slug,
                title = blog.title,
                description = blog.description ?: "",
                createdAt = blog.createdAt,
                userId = blog.userId
            )
        }
    }
}
