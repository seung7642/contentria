package com.contentria.api.user.application.dto

import com.contentria.api.user.domain.User

data class UserSummaryInfo(
    val username: String,
    val pictureUrl: String?,
) {
    companion object {
        fun from(user: User): UserSummaryInfo {
            return UserSummaryInfo(
                username = user.username!!,
                pictureUrl = user.pictureUrl,
            )
        }
    }
}