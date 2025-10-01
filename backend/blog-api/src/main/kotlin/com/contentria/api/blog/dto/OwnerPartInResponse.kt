package com.contentria.api.blog.dto

import com.contentria.api.user.domain.User

data class OwnerPartInResponse(
    val username: String?,
    val pictureUrl: String?,
) {
    companion object {
        fun from(user: User): OwnerPartInResponse {
            return OwnerPartInResponse(
                username = user.username,
                pictureUrl = user.pictureUrl,
            )
        }

        fun from(owner: OwnerInfo): OwnerPartInResponse {
            return OwnerPartInResponse(
                username = owner.username,
                pictureUrl = owner.pictureUrl,
            )
        }
    }
}
