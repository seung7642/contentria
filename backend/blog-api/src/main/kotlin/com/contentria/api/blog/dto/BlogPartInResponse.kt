package com.contentria.api.blog.dto

import com.contentria.api.blog.domain.Blog

data class BlogPartInResponse(
    val title: String,
    val slug: String,
    val description: String?
) {
    companion object {
        fun from(blog: Blog): BlogPartInResponse {
            return BlogPartInResponse(
                title = blog.title,
                slug = blog.slug,
                description = blog.description
            )
        }

        fun from(info: BlogInfo): BlogPartInResponse {
            return BlogPartInResponse(
                title = info.title,
                slug = info.slug,
                description = info.description
            )
        }
    }
}
