package com.contentria.api.blog.controller.dto

import com.contentria.api.blog.application.dto.BlogInfo
import java.time.ZonedDateTime
import java.util.*

data class CreateBlogResponse(
    val id: UUID,
    val slug: String,
    val title: String,
    val description: String?,
    val createdAt: ZonedDateTime
) {
    companion object {
        fun from(blogInfo: BlogInfo): CreateBlogResponse {
            return CreateBlogResponse(
                id = blogInfo.id,
                slug = blogInfo.slug,
                title = blogInfo.title,
                description = blogInfo.description,
                createdAt = blogInfo.createdAt
            )
        }
    }
}
