package com.contentria.api.blog.controller.dto

import com.contentria.api.blog.application.dto.BlogInfo
import java.util.UUID

data class BlogResponse(
    val id: UUID,
    val slug: String,
    val title: String,
    val description: String?
) {
    companion object {
        fun from(info: BlogInfo): BlogResponse {
            return BlogResponse(
                id = info.blogId,
                slug = info.slug,
                title = info.title,
                description = info.description
            )
        }
    }
}
