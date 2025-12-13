package com.contentria.api.auth.dto

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
