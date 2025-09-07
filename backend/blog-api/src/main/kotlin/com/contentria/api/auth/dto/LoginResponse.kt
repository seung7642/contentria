package com.contentria.api.auth.dto

import com.contentria.api.user.controller.UserInfoResponse

data class LoginResponse(
    val accessToken: String,
    val user: UserInfoResponse
)
