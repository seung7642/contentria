package com.demo.blog.auth.dto

data class VerificationCodeRequest(
    val email: String,
    val verificationCode: String,
    val password: String
)
