package com.contentria.api.blog.dto

import com.contentria.api.blog.domain.Blog

data class BlogInfoDto(
    val title: String,
    val slug: String,
    val description: String?
) {
    companion object {
        fun from(blog: Blog): BlogInfoDto {
            return BlogInfoDto(
                title = blog.title,
                slug = blog.slug,
                description = blog.description
            )
        }
    }
}
