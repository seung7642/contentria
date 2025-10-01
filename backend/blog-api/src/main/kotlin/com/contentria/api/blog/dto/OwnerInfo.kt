package com.contentria.api.blog.dto

import com.contentria.api.user.domain.User

data class OwnerInfo(
    val username: String,
    val pictureUrl: String?,
) {
    companion object {
        fun from(user: User): OwnerInfo {
            return OwnerInfo(
                username = user.username!!,
                pictureUrl = user.pictureUrl,
            )
        }
    }
}
