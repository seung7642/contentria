package com.contentria.api.blog.dto

import com.contentria.api.category.dto.CategoryResponse
import com.contentria.api.user.dto.UserSummaryResponse

data class BlogLayoutResponse(
    val blog: Blog,
    val owner: UserSummaryResponse,
    val categories: List<CategoryResponse>,
) {
    companion object {
        fun from(info: BlogLayoutInfo): BlogLayoutResponse {
            return BlogLayoutResponse(
                blog = Blog.from(info.blog),
                owner = UserSummaryResponse.from(info.owner),
                categories = info.categories.map { CategoryResponse.from(it) }
            )
        }
    }

    data class Blog(
        val title: String,
        val slug: String,
        val description: String?
    ) {
        companion object {
            fun from(blog: Blog): Blog {
                return Blog(
                    title = blog.title,
                    slug = blog.slug,
                    description = blog.description
                )
            }

            fun from(info: BlogInfo): Blog {
                return Blog(
                    title = info.title,
                    slug = info.slug,
                    description = info.description
                )
            }
        }
    }
}
