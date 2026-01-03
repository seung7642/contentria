package com.contentria.api.category.dto

import java.util.UUID

data class CreateCategoryResponse(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val level: Int
) {
    companion object {
        fun from(createCategoryInfo: CreateCategoryInfo): CreateCategoryResponse {
            return CreateCategoryResponse(
                id = createCategoryInfo.id,
                name = createCategoryInfo.name,
                slug = createCategoryInfo.slug,
                parentId = createCategoryInfo.parentId,
                level = createCategoryInfo.level
            )
        }
    }
}