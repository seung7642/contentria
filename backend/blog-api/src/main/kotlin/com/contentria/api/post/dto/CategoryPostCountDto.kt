package com.contentria.api.post.dto

import java.util.UUID

data class CategoryPostCountDto(
    val categoryId: UUID,
    val postCount: Long
)
