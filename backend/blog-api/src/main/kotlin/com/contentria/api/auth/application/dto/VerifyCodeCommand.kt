package com.contentria.api.auth.application.dto

data class VerifyCodeCommand(
    val email: String,
    val verificationCode: String
)
