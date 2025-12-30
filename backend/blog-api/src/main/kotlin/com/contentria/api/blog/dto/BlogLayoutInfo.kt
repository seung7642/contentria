package com.contentria.api.blog.dto

import com.contentria.api.user.dto.UserSummaryInfo

data class BlogLayoutInfo(
    val blog: BlogInfo,
    val owner: UserSummaryInfo,
    val categories: List<CategoryNodeInfo>
)
