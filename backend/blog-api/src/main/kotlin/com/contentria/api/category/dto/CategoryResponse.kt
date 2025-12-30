package com.contentria.api.category.dto

import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val level: Int, // 0 for top-level categories, 1 for sub-categories, etc.
    val postCount: Long
) {
    companion object {
        fun from(categoryInfo: CategoryInfo): CategoryResponse {
            return CategoryResponse(
                id = categoryInfo.id,
                name = categoryInfo.name,
                slug = categoryInfo.slug,
                parentId = categoryInfo.parentId,
                level = categoryInfo.level,
                postCount = categoryInfo.postCount
            )
        }
    }
}
