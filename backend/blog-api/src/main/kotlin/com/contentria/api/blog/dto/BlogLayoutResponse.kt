package com.contentria.api.blog.dto

import com.contentria.api.user.dto.UserSummaryResponse

data class BlogLayoutResponse(
    val blog: BlogPartInResponse,
    val owner: UserSummaryResponse,
    val categories: List<CategoryNodePartInResponse>,
) {
    companion object {
        fun from(info: BlogLayoutInfo): BlogLayoutResponse {
            return BlogLayoutResponse(
                blog = BlogPartInResponse.from(info.blog),
                owner = UserSummaryResponse.from(info.owner),
                categories = info.categories.map { CategoryNodePartInResponse.from(it) }
            )
        }
    }
}
