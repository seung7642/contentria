package com.contentria.api.user.application.dto

import com.contentria.api.user.domain.User
import java.util.UUID

data class UserPublicInfo(
    val userId: UUID,
    val username: String,
    val pictureUrl: String?,
) {
    companion object {
        fun from(user: User): UserPublicInfo {
            return UserPublicInfo(
                userId = user.id!!,
                username = user.username!!,
                pictureUrl = user.pictureUrl,
            )
        }
    }
}