package com.contentria.api.blog.dto

data class BlogLayoutResponse(
    val blog: BlogPartInResponse,
    val owner: OwnerPartInResponse,
    val categories: List<CategoryNodePartInResponse>,
) {
    companion object {
        fun from(info: BlogLayoutInfo): BlogLayoutResponse {
            return BlogLayoutResponse(
                blog = BlogPartInResponse.from(info.blog),
                owner = OwnerPartInResponse.from(info.owner),
                categories = info.categories.map { CategoryNodePartInResponse.from(it) }
            )
        }
    }
}
