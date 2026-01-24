package com.contentria.api.blog.controller.dto

import com.contentria.api.blog.application.dto.BlogLayoutInfo
import com.contentria.api.category.controller.dto.CategoryResponse
import com.contentria.api.user.application.dto.UserInfo
import java.util.*

data class BlogLayoutResponse(
    val blog: BlogResponse,
    val owner: OwnerResponse,
    val categories: List<CategoryResponse>,
) {
    data class OwnerResponse(
        val userId: UUID,
        val name: String,
        val profileImageUrl: String?
    ) {
        companion object {
            fun from(info: UserInfo): OwnerResponse {
                return OwnerResponse(
                    userId = info.userId,
                    name = info.name,
                    profileImageUrl = info.pictureUrl
                )
            }
        }
    }

    companion object {
        fun from(info: BlogLayoutInfo): BlogLayoutResponse {
            return BlogLayoutResponse(
                blog = BlogResponse.from(info.blog),
                owner = OwnerResponse.from(info.owner),
                categories = info.categories.map { CategoryResponse.from(it) }
            )
        }
    }
}
