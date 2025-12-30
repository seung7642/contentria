package com.contentria.api.auth.dto

import com.contentria.api.user.dto.CurrentUserResponse

data class VerifyCodeResult(
    val user: CurrentUserResponse,
    val accessToken: String,
    val refreshToken: String
)
