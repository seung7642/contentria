package com.contentria.api.auth.dto

import java.io.Serializable

data class VerificationCodeCacheDto(
    val code: String,
    val email: String,
    val name: String,
    val attempts: Int = 0,
) : Serializable {
    fun withIncrementedAttempts(): VerificationCodeCacheDto {
        return this.copy(attempts = this.attempts + 1)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}