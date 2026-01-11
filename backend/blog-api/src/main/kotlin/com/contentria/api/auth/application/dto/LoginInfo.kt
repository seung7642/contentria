package com.contentria.api.auth.application.dto

import com.contentria.api.user.controller.dto.CurrentUserResponse

data class LoginInfo(
    val user: CurrentUserResponse,
    val accessToken: String,
    val refreshToken: String
)
