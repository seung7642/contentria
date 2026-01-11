package com.contentria.api.auth.application.dto

data class LoginCommand(
    val email: String,
    val password: String?,
    val captcha: CaptchaCommand
)
