package com.contentria.api.auth.domain

import java.util.*

interface RefreshTokenRepository {

    fun findById(id: UUID): RefreshToken?
    fun save(refreshToken: RefreshToken): RefreshToken
    fun delete(refreshToken: RefreshToken)
    fun deleteAll(refreshTokens: List<RefreshToken>)

    fun findByToken(token: String): RefreshToken?

    fun findByUserId(userId: UUID): RefreshToken?

    fun deleteByToken(token: String): Int
}