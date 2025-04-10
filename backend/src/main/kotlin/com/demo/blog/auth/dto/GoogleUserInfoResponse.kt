package com.demo.blog.auth.dto

data class GoogleUserInfoResponse(
    val sub: String,
    val name: String,
    val email: String,
    val picture: String,
    val email_verified: Boolean
)
