package com.contentria.api.auth.application.dto

import java.util.UUID

data class RotatedTokenInfo(
    val newToken: String,
    val userId: UUID
)
