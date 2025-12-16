package com.contentria.api.user.controller

import com.contentria.api.blog.dto.BlogSummary
import com.contentria.api.user.domain.User
import java.util.UUID

data class UserInfoResponse(
    val userId: UUID?,
    val email: String,
    val name: String?,
    val profileImage: String?,
    val blogs: List<BlogSummary>? = emptyList()
) {
    companion object {
        fun from(user: User): UserInfoResponse {
            return UserInfoResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl,
                blogs = null,
            )
        }

        fun from(user: User, blogs: List<BlogSummary>): UserInfoResponse {
            return UserInfoResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl,
                blogs = blogs
            )
        }
    }
}