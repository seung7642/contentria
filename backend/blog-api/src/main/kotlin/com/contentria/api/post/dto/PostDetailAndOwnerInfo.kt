package com.contentria.api.post.dto

import com.contentria.api.blog.dto.OwnerInfo

data class PostDetailAndOwnerInfo(
    val post: PostDetailInfo,
    val owner: OwnerInfo
)