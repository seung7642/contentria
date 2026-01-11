package com.contentria.api.auth.application.dto

data class SignUpInitiateCommand(
    val email: String,
    val name: String,
    val password: String?,
    val captcha: CaptchaCommand
)
