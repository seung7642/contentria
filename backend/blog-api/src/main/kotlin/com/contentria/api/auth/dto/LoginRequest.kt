package com.contentria.api.auth.dto

import jakarta.validation.constraints.Email

data class LoginRequest(
    @field:Email(message = "Invalid email format")
    val email: String,
    val password: String?,
    override val recaptchaV2Token: String? = null,
    override val recaptchaV3Token: String? = null
) : RecaptchaRequest
