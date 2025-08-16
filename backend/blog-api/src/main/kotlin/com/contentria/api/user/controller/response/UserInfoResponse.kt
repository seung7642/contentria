package com.contentria.api.user.controller.response

data class UserInfoResponse(
    val userId: String,
    val email: String,
    val name: String?,
    val profileImage: String?
)