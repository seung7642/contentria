package com.demo.blog.dto

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val userId: Long,
    val email: String,
    val name: String?
)
