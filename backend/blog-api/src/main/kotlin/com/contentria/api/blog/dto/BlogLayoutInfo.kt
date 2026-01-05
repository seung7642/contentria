package com.contentria.api.blog.dto

import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.user.dto.UserSummaryInfo

data class BlogLayoutInfo(
    val blog: BlogInfo,
    val owner: UserSummaryInfo,
    val categories: List<CategoryInfo>
)
