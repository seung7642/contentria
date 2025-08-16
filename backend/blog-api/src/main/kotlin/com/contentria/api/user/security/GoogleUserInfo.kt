package com.contentria.api.user.security

data class GoogleUserInfo(
    val id: String,
    val email: String,
    val name: String,
    val picture: String?
)
