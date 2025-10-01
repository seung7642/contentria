package com.contentria.api.blog.dto

import java.time.ZonedDateTime
import java.util.*

data class CreateBlogResponse(
    val id: UUID,
    val slug: String,
    val title: String,
    val description: String?,
    val createdAt: ZonedDateTime
) {
    companion object {
        fun from(info: CreateBlogInfo): CreateBlogResponse {
            return CreateBlogResponse(
                id = info.id,
                slug = info.slug,
                title = info.title,
                description = info.description,
                createdAt = info.createdAt
            )
        }
    }
}
