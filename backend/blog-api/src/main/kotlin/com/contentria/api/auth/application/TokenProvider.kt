package com.contentria.api.auth.application

import com.contentria.api.auth.application.dto.AuthTokenCommand

interface TokenProvider {

    fun generateAccessToken(command: AuthTokenCommand): String

    fun validateToken(token: String): Boolean

    fun extractSubject(token: String): String
}