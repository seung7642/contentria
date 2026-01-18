package com.contentria.api.auth.controller.dto

import com.contentria.api.auth.application.dto.VerifyCodeInfo
import com.contentria.api.user.controller.dto.UserPrivateResponse

data class VerifyCodeResponse(
    val user: UserPrivateResponse,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun from(verifyCodeInfo: VerifyCodeInfo): VerifyCodeResponse {
            return VerifyCodeResponse(
                user = UserPrivateResponse.from(verifyCodeInfo.user),
                accessToken = verifyCodeInfo.accessToken,
                refreshToken = verifyCodeInfo.refreshToken
            )
        }
    }
}
