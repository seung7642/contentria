package com.contentria.api.auth.dto

data class SendOtpResponse(
    val message: String,
    val nextStep: String
)
