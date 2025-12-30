package com.contentria.api.user.dto

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