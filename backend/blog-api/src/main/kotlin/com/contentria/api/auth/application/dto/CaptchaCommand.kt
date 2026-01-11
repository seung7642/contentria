package com.contentria.api.auth.application.dto

data class CaptchaCommand(
    val token: String,
    val version: CaptchaVersion,
    val action: String,
    val clientIp: String?
)
