package com.contentria.api.blog.application.dto

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
        fun from(blogInfo: BlogInfo): CreateBlogInfo {
            return CreateBlogInfo(
                id = blogInfo.blogId!!,
                slug = blogInfo.slug,
                title = blogInfo.title,
                description = blogInfo.description,
                createdAt = blogInfo.createdAt
            )
        }
    }
}
