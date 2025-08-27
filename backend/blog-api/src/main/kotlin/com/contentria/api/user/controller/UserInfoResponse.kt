package com.contentria.api.user.controller

import com.contentria.api.user.domain.User

data class UserInfoResponse(
    val userId: String,
    val email: String,
    val name: String?,
    val profileImage: String?
) {
    companion object {
        fun from(user: User): UserInfoResponse {
            return UserInfoResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl
            )
        }
    }
}