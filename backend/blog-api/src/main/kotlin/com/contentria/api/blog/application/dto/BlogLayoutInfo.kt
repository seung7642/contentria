package com.contentria.api.blog.application.dto

import com.contentria.api.category.application.dto.CategoryInfo
import com.contentria.api.user.application.dto.UserPublicInfo

data class BlogLayoutInfo(
    val blog: BlogInfo,
    val owner: UserPublicInfo,
    val categories: List<CategoryInfo>
)
