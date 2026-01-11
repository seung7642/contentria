package com.contentria.api.auth.controller.dto

import com.contentria.api.user.controller.dto.CurrentUserResponse

data class LoginResponse(
    val user: CurrentUserResponse,
    val accessToken: String,
    val refreshToken: String
)
