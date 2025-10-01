package com.contentria.api.blog.dto

import com.contentria.api.blog.domain.Blog
import java.time.ZonedDateTime
import java.util.UUID

data class CreateBlogInfo(
    val id: UUID,
    val slug: String,
    val title: String,
    val description: String?,
    val createdAt: ZonedDateTime
) {
    companion object {
        fun from(blog: Blog): CreateBlogInfo {
            return CreateBlogInfo(
                id = blog.id!!,
                slug = blog.slug,
                title = blog.title,
                description = blog.description,
                createdAt = blog.createdAt
            )
        }
    }
}
