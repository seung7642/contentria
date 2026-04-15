package com.contentria.api.auth.controller.dto

import com.contentria.api.auth.application.dto.CaptchaCommand
import com.contentria.api.auth.application.dto.CaptchaVersion
import com.contentria.api.auth.application.dto.RecaptchaAction
import com.contentria.api.auth.application.dto.SignUpInitiateCommand
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpInitiateRequest(

    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Name cannot be empty")
    val name: String,

    val password: String?,
    override val recaptchaV2Token: String? = null,
    override val recaptchaV3Token: String? = null
) : RecaptchaRequest {

    fun toCommand(clientIp: String): SignUpInitiateCommand {
        val captchaCommand = when {
            hasRecaptchaV2Token() -> CaptchaCommand(
                token = recaptchaV2Token!!,
                version = CaptchaVersion.V2,
                action = RecaptchaAction.SIGN_UP.value,
                clientIp = clientIp
            )
            hasRecaptchaV3Token() -> CaptchaCommand(
                token = this.recaptchaV3Token!!,
                version = CaptchaVersion.V3,
                action = RecaptchaAction.SIGN_UP.value,
                clientIp = clientIp
            )
            else -> throw ContentriaException(ErrorCode.RECAPTCHA_TOKEN_MISSING)
        }

        return SignUpInitiateCommand(
            email = this.email,
            name = this.name,
            password = this.password,
            captcha = captchaCommand
        )
    }
}