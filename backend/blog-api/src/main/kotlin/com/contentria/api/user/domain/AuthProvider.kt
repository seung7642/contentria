package com.contentria.api.user.domain

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.STRING)
enum class AuthProvider {
    GOOGLE,
    EMAIL;

    companion object {
        fun fromString(provider: String?): AuthProvider {
            return when(provider?.uppercase()) {
                "GOOGLE" -> GOOGLE
                else -> EMAIL
            }
        }
    }
}