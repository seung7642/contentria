package com.contentria.api.user.controller.dto

import com.contentria.api.user.application.dto.UserPublicInfo

data class UserPublicResponse(
    val username: String,
    val pictureUrl: String?,
) {
    companion object {
        fun from(owner: UserPublicInfo): UserPublicResponse {
            return UserPublicResponse(
                username = owner.username,
                pictureUrl = owner.pictureUrl,
            )
        }
    }
}