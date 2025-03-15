package com.demo.blog.dto

data class GoogleUserInfo(
    val id: String,
    val email: String,
    val verified_email: Boolean,
    val name: String,
    val given_name: String?,
    val family_name: String?,
    val picture: String?
)
