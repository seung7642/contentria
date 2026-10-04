package com.contentria.api.blog.application.dto

import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.user.application.dto.UserInfo

data class BlogLayoutInfo(
    val blog: BlogInfo,
    val owner: UserInfo,
    val categories: List<CategoryInfo>
)
