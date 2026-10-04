package com.contentria.api.auth.domain

import java.util.*

interface RefreshTokenRepository {

    fun findById(id: UUID): RefreshToken?
    fun save(refreshToken: RefreshToken): RefreshToken
    fun delete(refreshToken: RefreshToken)
    fun deleteAll(refreshTokens: List<RefreshToken>)

    fun findByToken(token: String): RefreshToken?

    fun findByTokenForUpdate(token: String): RefreshToken?

    fun deleteByToken(token: String): Int

    /**
     * Deletes the oldest refresh tokens for [userId], keeping the [keep] most recent rows.
     * Returns the number of rows actually deleted.
     */
    fun pruneOldest(userId: UUID, keep: Int): Int
}
