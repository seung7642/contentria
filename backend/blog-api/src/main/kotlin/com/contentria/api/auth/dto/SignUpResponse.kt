package com.contentria.api.auth.dto

import com.contentria.api.user.dto.CurrentUserResponse

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: CurrentUserResponse
)
