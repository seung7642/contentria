package com.contentria.api.auth.controller.dto

data class VerifyCodeRequest(
    val email: String,
    val verificationCode: String,
)
