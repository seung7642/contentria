package com.contentria.api.auth.dto

import com.contentria.api.user.validation.UniqueEmail
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpInitiateRequest(

    @field:Email(message = "Invalid email format")
    @field:UniqueEmail(message = "Email address already in use")
    val email: String,

    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    val password: String?,
    val recaptchaV2Token: String? = null,
    val recaptchaV3Token: String? = null
) {
    fun hasRecaptchaV2Token(): Boolean = !recaptchaV2Token.isNullOrBlank()
    fun hasRecaptchaV3Token(): Boolean = !recaptchaV3Token.isNullOrBlank()
}