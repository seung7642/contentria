package com.contentria.api.auth.application.dto

data class RefreshedTokensInfo(
    val accessToken: String,
    val refreshToken: String
)