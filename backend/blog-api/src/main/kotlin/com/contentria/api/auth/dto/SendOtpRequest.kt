package com.contentria.api.auth.dto

import jakarta.validation.constraints.Email

data class SendOtpRequest(
    @field:Email(message = "Invalid email format")
    val email: String,
    override val recaptchaV2Token: String? = null,
    override val recaptchaV3Token: String? = null
) : RecaptchaRequest