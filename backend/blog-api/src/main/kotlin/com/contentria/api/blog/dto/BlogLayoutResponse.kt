package com.contentria.api.blog.dto

data class BlogLayoutResponse(
    val blog: BlogInfoDto,
    val owner: OwnerInfoDto,
    val categories: List<CategoryNodeDto>,
)
