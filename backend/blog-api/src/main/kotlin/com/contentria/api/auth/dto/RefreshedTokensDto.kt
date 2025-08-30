package com.contentria.api.auth.dto

data class RefreshedTokensDto(
    val accessToken: String,
    val refreshToken: String
)