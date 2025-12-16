package com.contentria.api.blog.dto

import com.contentria.api.blog.domain.Blog
import java.util.UUID

data class BlogSummary(
    val id: UUID,
    val title: String,
    val slug: String
) {
    companion object {
        fun from(blog: Blog): BlogSummary {
            return BlogSummary(
                id = blog.id!!,
                title = blog.title,
                slug = blog.slug
            )
        }
    }
}
