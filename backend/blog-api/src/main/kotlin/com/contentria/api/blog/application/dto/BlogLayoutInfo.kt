package com.contentria.api.blog.application.dto

import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.user.application.dto.UserSummaryInfo

data class BlogLayoutInfo(
    val blog: BlogSummaryInfo,
    val owner: UserSummaryInfo,
    val categories: List<CategoryInfo>
)
