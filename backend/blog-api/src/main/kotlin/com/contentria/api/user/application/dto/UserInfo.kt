package com.contentria.api.user.application.dto

import com.contentria.api.user.domain.User
import java.util.UUID

data class UserInfo(
    val userId: UUID,
    val email: String,
    val name: String,
    val pictureUrl: String?,
    val roles: List<String>
) {
    companion object {
        fun from(user: User): UserInfo {
            return UserInfo(
                userId = user.id!!,
                email = user.email,
                name = user.username!!,
                pictureUrl = user.pictureUrl,
                roles = user.userRoles.map { it.role.name }
            )
        }
    }
}
