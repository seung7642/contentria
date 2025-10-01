package com.contentria.api.blog.dto

data class BlogLayoutInfo(
    val blog: BlogInfo,
    val owner: OwnerInfo,
    val categories: List<CategoryNodeInfo>
)
