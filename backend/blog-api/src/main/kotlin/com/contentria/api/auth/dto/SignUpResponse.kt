package com.contentria.api.auth.dto

import com.contentria.api.user.controller.UserInfoResponse

data class SignUpResponse(
    val message: String,
    val accessToken: String,
    val refreshToken: String,
    val user: UserInfoResponse
)
