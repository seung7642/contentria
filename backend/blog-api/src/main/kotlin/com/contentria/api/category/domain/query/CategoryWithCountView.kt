package com.contentria.api.category.domain.query

import java.util.UUID

data class CategoryWithCountView(
    val id: UUID,
    val name: String,
    val slug: String,
    val parentId: UUID?,
    val postCount: Long
)
