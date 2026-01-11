package com.contentria.api.auth.application.dto

import java.util.UUID

data class AuthTokenInfo(
    val userId: UUID,
    val email: String,
    val roles: List<String>
)