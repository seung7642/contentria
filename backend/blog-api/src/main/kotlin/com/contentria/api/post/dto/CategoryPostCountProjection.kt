package com.contentria.api.post.dto

import java.util.UUID

data class CategoryPostCountProjection(
    val categoryId: UUID,
    val postCount: Long
)
