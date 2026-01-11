package com.contentria.api.post.domain.query

import java.util.UUID

data class CategoryPostCount(
    val categoryId: UUID,
    val postCount: Long
)