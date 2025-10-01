package com.contentria.api.blog.dto

import java.util.UUID

data class CategoryNodePartInResponse(
    val id: UUID?,
    val name: String,
    val slug: String,
    var postCount: Long,
    val children: List<CategoryNodePartInResponse>
) {
    companion object {
        fun from(categoryNodeInfo: CategoryNodeInfo): CategoryNodePartInResponse {
            return CategoryNodePartInResponse(
                id = categoryNodeInfo.id,
                name = categoryNodeInfo.name,
                slug = categoryNodeInfo.slug,
                postCount = categoryNodeInfo.postCount,
                children = categoryNodeInfo.children.map { from(it) }
            )
        }
    }
}
