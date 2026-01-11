package com.contentria.api.auth.controller.dto

interface RecaptchaRequest {
    val recaptchaV2Token: String?
    val recaptchaV3Token: String?

    fun hasRecaptchaV2Token(): Boolean = !recaptchaV2Token.isNullOrBlank()
    fun hasRecaptchaV3Token(): Boolean = !recaptchaV3Token.isNullOrBlank()
}