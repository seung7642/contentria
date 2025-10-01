package com.contentria.api.blog.dto

import java.util.UUID

data class CategoryNodeInfo(
    val id: UUID?,
    val name: String,
    val slug: String,
    var postCount: Long,
    val children: List<CategoryNodeInfo>
)