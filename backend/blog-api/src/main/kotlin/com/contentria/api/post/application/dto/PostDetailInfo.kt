package com.contentria.api.post.application.dto

import com.contentria.api.user.application.dto.UserInfo

data class PostDetailInfo(
    val post: PostContentInfo,
    val author: UserInfo,
    val blogSlug: String,
    val categoryName: String?
) {
    companion object {
        fun from(
            postContentInfo: PostContentInfo,
            userInfo: UserInfo,
            blogSlug: String,
            categoryName: String?
        ): PostDetailInfo {
            return PostDetailInfo(
                post = postContentInfo,
                author = userInfo,
                blogSlug = blogSlug,
                categoryName = categoryName
            )
        }
    }
}