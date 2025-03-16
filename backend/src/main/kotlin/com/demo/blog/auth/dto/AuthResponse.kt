package com.demo.blog.auth.dto

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val userId: String,
    val email: String,
    val name: String?
)
