package com.contentria.api.user.controller.dto

import com.contentria.api.blog.application.dto.BlogInfo
import com.contentria.api.user.application.dto.UserInfo
import java.time.ZonedDateTime
import java.util.*

data class CurrentUserResponse(
    val userId: UUID?,
    val email: String,
    val name: String?,
    val profileImage: String?,
    val blogResponses: List<BlogResponse> = emptyList()
) {
    data class BlogResponse(
        val id: UUID,
        val slug: String,
        val title: String,
        val description: String? = null,
        val createdAt: ZonedDateTime
    ) {
        companion object {
            fun from(blogInfo: BlogInfo): BlogResponse {
                return BlogResponse(
                    id = blogInfo.blogId,
                    slug = blogInfo.slug,
                    title = blogInfo.title,
                    description = blogInfo.description,
                    createdAt = blogInfo.createdAt
                )
            }
        }
    }

    companion object {
        fun from(userInfo: UserInfo, blogInfos: List<BlogInfo>): CurrentUserResponse {
            return CurrentUserResponse(
                userId = userInfo.userId,
                email = userInfo.email,
                name = userInfo.name,
                profileImage = userInfo.pictureUrl,
                blogResponses = blogInfos.map { BlogResponse.from(it) }
            )
        }
    }
}