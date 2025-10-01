package com.contentria.api.post.dto

import com.contentria.api.blog.dto.OwnerPartInResponse

data class PostDetailResponse(
    val post: PostPartInResponse,
    val owner: OwnerPartInResponse
) {
    companion object {
        fun from(postDetailAndOwnerInfo: PostDetailAndOwnerInfo): PostDetailResponse {
            return PostDetailResponse(
                post = PostPartInResponse.from(postDetailAndOwnerInfo.post),
                owner = OwnerPartInResponse.from(postDetailAndOwnerInfo.owner)
            )
        }
    }
}
