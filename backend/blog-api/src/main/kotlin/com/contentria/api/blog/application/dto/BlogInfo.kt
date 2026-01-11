package com.contentria.api.blog.application.dto

import com.contentria.api.blog.domain.Blog

data class BlogInfo(
    val title: String,
    val slug: String,
    val description: String
) {
    companion object {
        fun from(blog: Blog): BlogInfo {
            return BlogInfo(
                title = blog.title,
                slug = blog.slug,
                description = blog.description ?: ""
            )
        }
    }
}
