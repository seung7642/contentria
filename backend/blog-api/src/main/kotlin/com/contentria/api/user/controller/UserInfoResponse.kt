package com.contentria.api.user.controller

import com.contentria.api.user.domain.User
import java.util.UUID

data class UserInfoResponse(
    val userId: UUID?,
    val email: String,
    val name: String?,
    val profileImage: String?,
    val slugs: List<String>? = emptyList(),
) {
    companion object {
        fun from(user: User): UserInfoResponse {
            return UserInfoResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl,
                slugs = null,
            )
        }

        fun from(user: User, slugs: List<String>): UserInfoResponse {
            return UserInfoResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl,
                slugs = slugs,
            )
        }
    }
}