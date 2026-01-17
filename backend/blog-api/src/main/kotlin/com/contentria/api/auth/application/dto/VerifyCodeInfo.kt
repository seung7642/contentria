package com.contentria.api.auth.application.dto

import com.contentria.api.user.application.dto.UserInfo

data class VerifyCodeInfo(
    val user: UserInfo,
    val accessToken: String,
    val refreshToken: String
)
