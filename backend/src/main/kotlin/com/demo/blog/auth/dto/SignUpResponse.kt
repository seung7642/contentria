package com.demo.blog.auth.dto

import com.demo.blog.user.controller.response.UserInfoResponse

data class SignUpResponse(
    val status: String,
    val message: String,
    val authToken: String,
    val user: UserInfoResponse
)
