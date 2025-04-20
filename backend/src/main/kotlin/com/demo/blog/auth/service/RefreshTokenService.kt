package com.demo.blog.auth.service

import com.demo.blog.auth.domain.RefreshToken
import com.demo.blog.auth.repository.RefreshTokenRepository
import com.demo.blog.common.exception.RefreshTokenExpiredException
import com.demo.blog.common.exception.RefreshTokenNotFoundException
import com.demo.blog.common.properties.AppProperties
import com.demo.blog.user.domain.User
import com.demo.blog.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import org.springframework.data.repository.findByIdOrNull

private val logger = KotlinLogging.logger {}

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val appProperties: AppProperties
) {

    @Transactional
    fun createOrUpdateOpaqueRefreshToken(userId: String): String {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UsernameNotFoundException("User not found with id: $userId")

        val expiryDate = Instant.now().plusMillis(appProperties.auth.jwt.refreshTokenExpirationMs)
        val tokenValue = UUID.randomUUID().toString() // Opaque Token 생성

        var refreshToken = refreshTokenRepository.findByUser(user)
        if (refreshToken == null) {
            refreshToken = RefreshToken(user = user, token = tokenValue, expiryDate = expiryDate)
            logger.info { "Creating new refresh token for user ID: $userId" }
        } else {
            // 기존 토큰 업데이트 (Rotation 효과)
            refreshToken.token = tokenValue
            refreshToken.expiryDate = expiryDate
            logger.info { "Updating existing refresh token for user ID: $userId" }
        }

        refreshTokenRepository.save(refreshToken)

        return tokenValue
    }

    @Transactional
    fun findByTokenAndVerify(token: String): RefreshToken {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: throw RefreshTokenNotFoundException()

        if (refreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            logger.warn { "Refresh token expired and deleted: ${refreshToken.token}" }
            throw RefreshTokenExpiredException()
        }

        return refreshToken
    }

    @Transactional
    fun deleteRefreshTokenByUser(user: User): Int {
        val deletedCount = refreshTokenRepository.deleteByUser(user)
        logger.info { "Deleted $deletedCount refresh token(s) for user: ${user.email}" }
        return deletedCount
    }
}