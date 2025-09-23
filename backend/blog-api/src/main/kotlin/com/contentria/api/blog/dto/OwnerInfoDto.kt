package com.contentria.api.blog.dto

import com.contentria.api.user.domain.User
import java.util.UUID

data class OwnerInfoDto(
    val username: String?,
    val pictureUrl: String?,
) {
    companion object {
        fun from(user: User): OwnerInfoDto {
            return OwnerInfoDto(
                username = user.username,
                pictureUrl = user.pictureUrl,
            )
        }
    }
}
