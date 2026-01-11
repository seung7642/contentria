package com.contentria.api.auth.application.dto

data class SendOtpCommand(
    val email: String,
    val captcha: CaptchaCommand
)
