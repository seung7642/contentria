package com.demo.blog.auth.dto

data class GoogleUserInfo(
    val id: String,
    val email: String,
    val name: String,
    val picture: String?
)
