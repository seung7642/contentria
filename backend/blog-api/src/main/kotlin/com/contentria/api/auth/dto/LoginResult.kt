package com.contentria.api.auth.dto

import com.contentria.api.user.controller.UserInfoResponse

data class LoginResult(
    val user: UserInfoResponse,
    val accessToken: String,
    val refreshToken: String
)
