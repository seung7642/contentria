package com.contentria.api.category.dto

import java.util.UUID

data class CategoryInfo(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val level: Int, // 0 for top-level categories, 1 for sub-categories, etc.
    val postCount: Long
)
