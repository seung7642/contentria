package com.contentria.api.auth.dto

import com.contentria.api.user.dto.CurrentUserResponse

data class VerifyCodeResponse(
    val user: CurrentUserResponse
)
