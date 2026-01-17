package com.contentria.api.user.application.dto

import com.contentria.api.blog.application.dto.BlogInfo
import java.util.UUID

data class CurrentUserInfo(
    val id: UUID,
    val email: String,
    val name: String,
    val profileImage: String?,
    val blogInfos: List<BlogInfo>
)
