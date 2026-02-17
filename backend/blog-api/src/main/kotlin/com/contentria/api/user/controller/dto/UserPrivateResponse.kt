package com.contentria.api.user.controller.dto

import com.contentria.api.user.application.dto.UserInfo
import java.util.*

data class UserPrivateResponse(
    val userId: UUID?,
    val email: String,
    val username: String?,
    val nickname: String,
    val profileImage: String?
) {
    companion object {
        fun from(userInfo: UserInfo): UserPrivateResponse {
            return UserPrivateResponse(
                userId = userInfo.userId,
                email = userInfo.email,
                username = userInfo.username,
                nickname = userInfo.nickname,
                profileImage = userInfo.pictureUrl
            )
        }
    }
}