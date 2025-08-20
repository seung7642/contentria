package com.contentria.api.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignUpInitiateRequest(
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    @field:Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    val password: String,

    val recaptchaV2Token: String? = null,
    val recaptchaV3Token: String? = null
)