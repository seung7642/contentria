package com.contentria.api.auth.dto

import java.io.Serializable

data class VerificationCodeCacheDto(
    val code: String,
    val email: String,
    val name: String?,
    val attempts: Int = 0,
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}