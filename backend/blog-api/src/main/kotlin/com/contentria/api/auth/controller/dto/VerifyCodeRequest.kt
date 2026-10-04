package com.contentria.api.auth.controller.dto

import com.contentria.api.auth.application.dto.VerifyCodeCommand

data class VerifyCodeRequest(
    val email: String,
    val verificationCode: String,
) {
    fun toCommand(): VerifyCodeCommand {
        return VerifyCodeCommand(
            email = this.email,
            verificationCode = this.verificationCode
        )
    }
}
