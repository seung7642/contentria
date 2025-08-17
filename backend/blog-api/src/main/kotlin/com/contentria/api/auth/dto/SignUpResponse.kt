package com.contentria.api.auth.dto

import com.contentria.api.user.controller.UserInfoResponse

data class SignUpResponse(
    val status: String,
    val message: String,
    val authToken: String,
    val user: UserInfoResponse
)
