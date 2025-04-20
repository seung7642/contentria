package com.demo.blog.auth.controller

import com.demo.blog.auth.service.JwtService
import com.demo.blog.auth.service.RefreshTokenService
import com.demo.blog.common.properties.AppProperties
import com.demo.blog.user.security.CustomUserDetailsService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/auth")
class AuthController(
    private val refreshTokenService: RefreshTokenService,
    private val jwtService: JwtService,
    private val userDetailsService: CustomUserDetailsService,
    private val appProperties: AppProperties
) {

    private val accessTokenCookieMaxAge: Int = TimeUnit.MILLISECONDS.toSeconds(appProperties.auth.jwt.accessTokenExpirationMs).toInt()
    private val refreshTokenCookieMaxAge: Int = TimeUnit.MILLISECONDS.toSeconds(appProperties.auth.jwt.refreshTokenExpirationMs).toInt()
    private val accessTokenCookieName: String = appProperties.auth.cookie.accessTokenName
    private val refreshTokenCookieName: String = appProperties.auth.cookie.refreshTokenName
    private val accessTokenPath: String = appProperties.auth.cookie.accessTokenPath
    private val refreshTokenPath: String = appProperties.auth.cookie.refreshTokenPath

    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue(name = "\${app.auth.cookie.refresh-token-name}", required = true) refreshTokenValue: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        try {
            // 1. DB에서 Refresh Token 조회 및 만료 검증
            val refreshToken = refreshTokenService.findByTokenAndVerify(refreshTokenValue)

            // 2. 유효하다면 User 정보 로드
            val user = refreshToken.user
            val userDetails = userDetailsService.loadUserByUsername(user.email)

            // 3. 새 Access Token 생성
            val newAccessToken = jwtService.generateAccessToken(userDetails)

            // 4. Refresh Token Rotation
            val newOpaqueRefreshTokenValue = refreshTokenService.createOrUpdateOpaqueRefreshToken(user.id)

            // 5. 새 Access Token 쿠키 생성 및 설정
            val accessTokenCookie = Cookie(accessTokenCookieName, newAccessToken).apply {
                isHttpOnly = true
                secure = request.isSecure
                path = accessTokenPath
                maxAge = accessTokenCookieMaxAge
            }
            response.addCookie(accessTokenCookie)

            // 6. 새로 생성된 Refresh Token 쿠키 생성 및 설정
            val newRefreshTokenCookie = Cookie(refreshTokenCookieName, newOpaqueRefreshTokenValue).apply {
                isHttpOnly = true
                secure = request.isSecure
                path = refreshTokenPath
                maxAge = refreshTokenCookieMaxAge
            }
            response.addCookie(newRefreshTokenCookie)

            logger.info { "Successfully refreshed token for user: ${user.email}" }
            return ResponseEntity.ok().body(mapOf("message" to "Token refreshed successfully"))
        } catch (e: UsernameNotFoundException) {
            logger.error(e) { "User associated with refresh token not found: ${e.message}" }
            clearTokens(response)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found")
        } catch(e: Exception) {
            logger.error(e) { "Unexpected error during token refresh" }
            clearTokens(response)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Internal server error")
        }
    }

    private fun clearTokens(response: HttpServletResponse) {
        val accessCookie = Cookie(accessTokenCookieName, null).apply {
            maxAge = 0;
            path = accessTokenPath
        }
        val refreshCookie = Cookie(refreshTokenCookieName, null).apply {
            maxAge = 0;
            path = refreshTokenPath
        }
        response.addCookie(accessCookie)
        response.addCookie(refreshCookie)
    }
}