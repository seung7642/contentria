package com.contentria.api.user.controller.dto

import com.contentria.api.user.application.dto.UserSummaryInfo

data class UserSummaryResponse(
    val username: String,
    val pictureUrl: String?,
) {
    companion object {
        fun from(owner: UserSummaryInfo): UserSummaryResponse {
            return UserSummaryResponse(
                username = owner.username,
                pictureUrl = owner.pictureUrl,
            )
        }
    }
}