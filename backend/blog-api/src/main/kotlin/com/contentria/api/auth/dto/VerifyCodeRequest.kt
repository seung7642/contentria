package com.contentria.api.auth.dto

data class VerifyCodeRequest(
    val email: String,
    val verificationCode: String,
)
