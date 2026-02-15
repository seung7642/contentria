package com.contentria.api.post.application.dto

import com.contentria.api.user.application.dto.UserInfo
import java.util.UUID

data class PostDetailInfo(
    val post: PostContentInfo,
    val author: UserInfo,
    val blogId: UUID,
    val blogSlug: String,
    val categoryId: UUID?,
    val categoryName: String?
) {
    companion object {
        fun from(
            postContentInfo: PostContentInfo,
            userInfo: UserInfo,
            blogId: UUID,
            blogSlug: String,
            categoryId: UUID?,
            categoryName: String?
        ): PostDetailInfo {
            return PostDetailInfo(
                post = postContentInfo,
                author = userInfo,
                blogId = blogId,
                blogSlug = blogSlug,
                categoryId = categoryId,
                categoryName = categoryName
            )
        }
    }
}