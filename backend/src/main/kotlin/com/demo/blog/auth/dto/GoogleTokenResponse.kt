package com.demo.blog.auth.dto

data class GoogleTokenResponse(
    val access_token: String,
    val expires_in: Int,
    val token_type: String,
    val refresh_token: String?,
    val id_token: String
)