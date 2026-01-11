package com.contentria.api.auth.controller.dto

import com.contentria.api.auth.application.dto.CaptchaCommand
import com.contentria.api.auth.application.dto.CaptchaVersion
import com.contentria.api.auth.application.dto.RecaptchaAction
import com.contentria.api.auth.application.dto.SendOtpCommand
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import jakarta.validation.constraints.Email

data class SendOtpRequest(
    @field:Email(message = "Invalid email format")
    val email: String,
    override val recaptchaV2Token: String? = null,
    override val recaptchaV3Token: String? = null
) : RecaptchaRequest {

    fun toCommand(clientIp: String): SendOtpCommand {
        val captchaCommand = when {
            hasRecaptchaV2Token() -> CaptchaCommand(
                token = recaptchaV2Token!!,
                version = CaptchaVersion.V2,
                action = RecaptchaAction.SEND_OTP.value,
                clientIp = clientIp
            )
            hasRecaptchaV3Token() -> CaptchaCommand(
                token = this.recaptchaV3Token!!,
                version = CaptchaVersion.V3,
                action = RecaptchaAction.SEND_OTP.value,
                clientIp = clientIp
            )
            else -> throw ContentriaException(ErrorCode.RECAPTCHA_TOKEN_MISSING)
        }

        return SendOtpCommand(
            email = this.email,
            captcha = captchaCommand
        )
    }
}