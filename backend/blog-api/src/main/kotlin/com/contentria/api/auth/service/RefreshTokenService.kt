package com.contentria.api.auth.service

import com.contentria.api.auth.domain.RefreshToken
import com.contentria.api.auth.dto.RefreshedTokensDto
import com.contentria.api.auth.repository.RefreshTokenRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    private val appProperties: AppProperties,
    private val jwtService: JwtService
) {

    @Transactional
    fun refreshTokens(oldRefreshTokenValue: String): RefreshedTokensDto {
        // 1. DB에서 Refresh Token 조회 및 만료 검증
        val refreshToken = findByTokenAndVerify(oldRefreshTokenValue)

        // 2. 유효하다면 User 정보 로드
        val user = refreshToken.user

        // 3. 새 Access Token 생성
        val newAccessToken = jwtService.generateAccessToken(user)

        // 4. Refresh Token Rotation
        val newOpaqueRefreshToken = createOrUpdateOpaqueRefreshToken(user.id!!)

        // 5. 결과 DTO 반환
        return RefreshedTokensDto(
            accessToken = newAccessToken,
            refreshToken = newOpaqueRefreshToken
        )
    }

    @Transactional
    fun createOrUpdateOpaqueRefreshToken(userId: UUID): String {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ContentriaException(ErrorCode.USER_NOT_FOUND)

        val expiryDate = Instant.now().plus(appProperties.auth.jwt.refreshTokenExpiration)
        val tokenValue = UUID.randomUUID().toString() // Opaque Token 생성

        var refreshToken = refreshTokenRepository.findByUser(user)
        if (refreshToken == null) {
            refreshToken = RefreshToken(user = user, token = tokenValue, expiryDate = expiryDate)
            log.info { "Creating new refresh token for user ID: $userId" }
        } else {
            // 기존 토큰 업데이트 (Rotation 효과)
            refreshToken.token = tokenValue
            refreshToken.expiryDate = expiryDate
            log.info { "Updating existing refresh token for user ID: $userId" }
        }

        refreshTokenRepository.save(refreshToken)

        return tokenValue
    }

    private fun findByTokenAndVerify(token: String): RefreshToken {
        val refreshToken = refreshTokenRepository.findByToken(token)
            ?: throw ContentriaException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)

        if (refreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken)
            log.warn { "Refresh token expired and deleted: ${refreshToken.token}" }
            throw ContentriaException(ErrorCode.REFRESH_TOKEN_EXPIRED)
        }

        return refreshToken
    }

    @Transactional
    fun deleteRefreshTokenByToken(token: String): Int {
        val deletedCount = refreshTokenRepository.deleteByToken(token)
        log.info { "Deleted refresh token(s): ${token}" }
        return deletedCount
    }
}