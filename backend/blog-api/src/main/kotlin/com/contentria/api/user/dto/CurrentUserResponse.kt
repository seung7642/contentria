package com.contentria.api.user.dto

import com.contentria.api.blog.dto.BlogSummary
import com.contentria.api.user.domain.User
import java.util.UUID

data class CurrentUserResponse(
    val userId: UUID?,
    val email: String,
    val name: String?,
    val profileImage: String?,
    val blogs: List<BlogSummary>? = emptyList()
) {
    companion object {
        fun from(user: User): CurrentUserResponse {
            return CurrentUserResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl,
                blogs = null,
            )
        }

        fun from(user: User, blogs: List<BlogSummary>): CurrentUserResponse {
            return CurrentUserResponse(
                userId = user.id,
                email = user.email,
                name = user.username,
                profileImage = user.pictureUrl,
                blogs = blogs
            )
        }
    }
}