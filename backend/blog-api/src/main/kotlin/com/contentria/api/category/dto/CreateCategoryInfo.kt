package com.contentria.api.category.dto

import com.contentria.api.category.domain.Category
import java.util.UUID

data class CreateCategoryInfo(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val level: Int
) {
    companion object {
        fun from(category: Category): CreateCategoryInfo {
            return CreateCategoryInfo(
                id = category.id!!,
                name = category.name,
                slug = category.slug,
                parentId = category.parent?.id,
                level = category.parent?.let { 1 } ?: 0
            )
        }
    }
}