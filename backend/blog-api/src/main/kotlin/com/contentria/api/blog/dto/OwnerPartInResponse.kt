package com.contentria.api.blog.dto

data class OwnerPartInResponse(
    val username: String?,
    val pictureUrl: String?,
) {
    companion object {
        fun from(owner: OwnerInfo): OwnerPartInResponse {
            return OwnerPartInResponse(
                username = owner.username,
                pictureUrl = owner.pictureUrl,
            )
        }
    }
}
