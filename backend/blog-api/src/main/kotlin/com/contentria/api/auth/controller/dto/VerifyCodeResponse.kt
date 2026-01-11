package com.contentria.api.auth.controller.dto

import com.contentria.api.user.controller.dto.CurrentUserResponse

data class VerifyCodeResponse(
    val user: CurrentUserResponse
)
