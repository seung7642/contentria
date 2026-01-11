package com.contentria.api.auth.application.dto

data class RefreshedTokensDto(
    val accessToken: String,
    val refreshToken: String
)