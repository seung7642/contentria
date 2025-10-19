package com.contentria.api.post.repository

import java.util.UUID

data class CategoryPostCountProjection(
    val categoryId: UUID,
    val postCount: Long
)