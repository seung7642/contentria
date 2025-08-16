package com.contentria.api.auth.dto

data class VerificationCodeRequest(
    val email: String,
    val verificationCode: String,
    val password: String
)
