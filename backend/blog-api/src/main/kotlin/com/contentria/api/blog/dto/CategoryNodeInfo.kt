package com.contentria.api.blog.dto

import com.contentria.api.category.Category
import java.util.UUID

data class CategoryNodeInfo(
    val id: UUID?,
    val name: String,
    val slug: String,
    var postCount: Long,
    val children: List<CategoryNodeInfo>
) {
    companion object {
        fun from(category: Category, postCount: Long = 0L): CategoryNodeInfo {
            return CategoryNodeInfo(
                id = category.id,
                name = category.name,
                slug = category.slug,
                postCount = postCount,
                children = emptyList()
            )
        }
    }
}