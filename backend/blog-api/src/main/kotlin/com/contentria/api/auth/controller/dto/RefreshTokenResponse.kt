package com.contentria.api.auth.controller.dto

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
