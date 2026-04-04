package com.contentria.api.auth.application

import com.contentria.api.auth.domain.RefreshToken
import com.contentria.api.auth.domain.RefreshTokenRepository
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
    private val appProperties: AppProperties
) {

    @Transactional
    fun upsertRefreshToken(userId: UUID): String {
        val expiryDate = Instant.now().plus(appProperties.auth.jwt.refreshTokenExpiration)
        val tokenValue = UUID.randomUUID().toString()

        val refreshToken = refreshTokenRepository.findByUserId(userId)
            ?.apply {
                this.token = tokenValue
                this.expiryDate = expiryDate
            }
            ?: RefreshToken(userId = userId, token = tokenValue, expiryDate = expiryDate)

        refreshTokenRepository.save(refreshToken)
        return tokenValue
    }

    @Transactional
    fun findValidToken(token: String): RefreshToken {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: throw ContentriaException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)

        if (refreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            log.warn { "Expired refresh token deleted: userId=${refreshToken.userId}" }
            throw ContentriaException(ErrorCode.REFRESH_TOKEN_EXPIRED)
        }

        return refreshToken
    }

    @Transactional
    fun deleteRefreshTokenByToken(token: String): Int {
        val deletedCount = refreshTokenRepository.deleteByToken(token)
        log.debug { "Refresh token deleted: count=$deletedCount" }
        return deletedCount
    }
}