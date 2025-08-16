package com.contentria.api.auth.service

import com.contentria.api.auth.domain.RefreshToken

interface RefreshTokenService {

    fun createOrUpdateOpaqueRefreshToken(userId: String): String

    fun findByTokenAndVerify(token: String): RefreshToken

    fun deleteRefreshTokenByToken(token: String): Int
}