package com.contentria.api.auth.application

import com.contentria.api.auth.application.dto.RotatedTokenInfo
import com.contentria.api.auth.domain.RefreshToken
import com.contentria.api.auth.domain.RefreshTokenRepository
import com.contentria.api.auth.infrastructure.RefreshTokenGraceCache
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.api.global.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val appProperties: AppProperties,
    private val graceCache: RefreshTokenGraceCache
) {

    /**
     * Issues a new refresh token for [userId]. Each call inserts a fresh row, so a user can
     * hold concurrent sessions across devices. To prevent unbounded accumulation, rows beyond
     * `app.auth.refresh-token.max-per-user` (oldest by created_at) are pruned in the same
     * transaction.
     */
    @Transactional
    fun createRefreshToken(userId: UUID): String {
        val expiryDate = Instant.now().plus(appProperties.auth.jwt.refreshTokenExpiration)
        val tokenValue = UUID.randomUUID().toString()

        refreshTokenRepository.save(
            RefreshToken(userId = userId, token = tokenValue, expiryDate = expiryDate)
        )

        val maxPerUser = appProperties.auth.refreshToken.maxPerUser
        val pruned = refreshTokenRepository.pruneOldest(userId, maxPerUser)
        if (pruned > 0) {
            log.info { "Pruned $pruned oldest refresh token(s) for userId=$userId (cap=$maxPerUser)" }
        }

        return tokenValue
    }

    /**
     * Atomically rotates a refresh token under a row-level exclusive lock.
     *
     * Concurrent callers with the same `oldToken` are serialized by the DB lock; only one
     * actually performs the rotation. The cache write happens inside the same transaction —
     * the lock is still held — so a follow-up caller that reads the cache after the lock
     * is released is guaranteed to see the rotated value. Using `@TransactionalEventListener`
     * (AFTER_COMMIT) would leave a window between commit (lock release) and listener execution
     * where a concurrent caller could miss both the row and the cache.
     *
     * @return rotated token info, or null if no row matched (caller should fall back to the
     *         grace cache via [lookupGrace]).
     */
    @Transactional
    fun rotate(oldToken: String): RotatedTokenInfo? {
        val refreshToken = refreshTokenRepository.findByTokenForUpdate(oldToken)
            ?: return null

        if (refreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            log.warn { "Expired refresh token deleted: userId=${refreshToken.userId}" }
            throw ContentriaException(ErrorCode.REFRESH_TOKEN_EXPIRED)
        }

        val newValue = UUID.randomUUID().toString()
        val newExpiry = Instant.now().plus(appProperties.auth.jwt.refreshTokenExpiration)
        refreshToken.token = newValue
        refreshToken.expiryDate = newExpiry
        // dirty checking flushes UPDATE at commit

        val rotated = RotatedTokenInfo(newToken = newValue, userId = refreshToken.userId)
        graceCache.put(oldToken, rotated)
        return rotated
    }

    fun lookupGrace(oldToken: String): RotatedTokenInfo? {
        return graceCache.get(oldToken)
    }

    @Transactional
    fun deleteRefreshTokenByToken(token: String): Int {
        val deletedCount = refreshTokenRepository.deleteByToken(token)
        log.debug { "Refresh token deleted: count=$deletedCount" }
        return deletedCount
    }
}