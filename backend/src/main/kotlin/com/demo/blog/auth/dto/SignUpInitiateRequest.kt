package com.demo.blog.auth.dto

data class SignUpInitiateRequest(
    val email: String,
    val name: String,
    val password: String,
    val recaptchaV2Token: String? = null,
    val recaptchaV3Token: String? = null
)