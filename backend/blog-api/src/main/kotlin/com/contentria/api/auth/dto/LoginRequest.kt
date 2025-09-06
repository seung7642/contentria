package com.contentria.api.auth.dto

import com.contentria.api.user.validation.UniqueEmail
import jakarta.validation.constraints.Email

data class LoginRequest(
    @field:Email(message = "Invalid email format")
    @field:UniqueEmail(message = "Email address already in use")
    val email: String,
    val password: String?,
    val recaptchaV2Token: String? = null,
    val recaptchaV3Token: String? = null
) {
    fun hasRecaptchaV2Token(): Boolean = !recaptchaV2Token.isNullOrBlank()
    fun hasRecaptchaV3Token(): Boolean = !recaptchaV3Token.isNullOrBlank()
}
