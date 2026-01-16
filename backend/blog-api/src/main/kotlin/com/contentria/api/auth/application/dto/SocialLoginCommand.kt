package com.contentria.api.auth.application.dto

import com.contentria.api.user.domain.AuthProvider

data class SocialLoginCommand(
    val email: String,
    val name: String,
    val picture: String?,
    val provider: AuthProvider,
    val providerId: String,
)