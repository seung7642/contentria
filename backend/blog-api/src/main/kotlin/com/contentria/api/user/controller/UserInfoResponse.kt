package com.contentria.api.user.controller

data class UserInfoResponse(
    val userId: String,
    val email: String,
    val name: String?,
    val profileImage: String?
)