package com.contentria.api.auth.controller.dto

import com.contentria.api.user.application.dto.UserInfo
import com.contentria.api.user.controller.dto.UserPrivateResponse

data class LoginResponse(
    val user: UserPrivateResponse,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun from(userInfo: UserInfo, accessToken: String, refreshToken: String): LoginResponse {
            return LoginResponse(
                user = UserPrivateResponse.from(userInfo),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }
}
