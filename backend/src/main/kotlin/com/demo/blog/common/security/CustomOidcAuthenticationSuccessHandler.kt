package com.demo.blog.common.security

import com.demo.blog.auth.service.JwtService
import com.demo.blog.auth.service.RefreshTokenService
import com.demo.blog.common.exception.OidcAuthenticationProcessingException
import com.demo.blog.common.properties.AppProperties
import com.demo.blog.user.domain.User
import com.demo.blog.user.security.CustomUserDetailsService
import com.demo.blog.user.security.GoogleUserInfo
import com.demo.blog.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Component
class CustomOidcAuthenticationSuccessHandler(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val appProperties: AppProperties,
    private val customUserDetailsService: CustomUserDetailsService,
    private val refreshTokenService: RefreshTokenService
) : SimpleUrlAuthenticationSuccessHandler() {

    private val accessTokenCookieMaxAge: Int = TimeUnit.MILLISECONDS.toSeconds(appProperties.auth.jwt.accessTokenExpirationMs).toInt()
    private val refreshTokenCookieMaxAge: Int = TimeUnit.MILLISECONDS.toSeconds(appProperties.auth.jwt.refreshTokenExpirationMs).toInt()
    private val accessTokenCookieName: String = appProperties.auth.cookie.accessTokenName
    private val refreshTokenCookieName: String = appProperties.auth.cookie.refreshTokenName
    private val accessTokenPath: String = appProperties.auth.cookie.accessTokenPath
    private val refreshTokenPath: String = appProperties.auth.cookie.refreshTokenPath
    private val frontendRedirectUrl: String = appProperties.auth.oidc.successRedirectUrl

    @Transactional
    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 1. OIDC 사용자 정보 가져오기
        val oidcUser = authentication.principal as? OidcUser
            ?: run {
                logger.error {"Authentication principal is not an OidcUser: ${authentication.principal}"}
                throw OidcAuthenticationProcessingException("Invalid user type")
            }

        // 2. GoogleUserInfo 객체 생성 (ID 토큰 클레임 사용)
        val googleUserInfo = GoogleUserInfo(
            id = oidcUser.subject,
            email = oidcUser.email ?: run {
                logger.error { "Email not found in OIDC claims for user: ${oidcUser.subject}" }
                throw OidcAuthenticationProcessingException("Email not found in OIDC claims")
            },
            name = oidcUser.fullName ?: oidcUser.givenName ?: oidcUser.subject,
            picture = oidcUser.picture
        )
        logger.info {"Processing OIDC login for user: ${googleUserInfo.email}"}

        try {
            // 3. 사용자 정보 데이터베이스에 저장 또는 업데이트
            val user: User = userService.upsertGoogleUser(googleUserInfo)

            // 4. 자체 JWT 생성을 위해 UserDetails 로드 (데이터베이스에 저장된 역할 등 최신 정보 반영을 위함)
            val userDetails = customUserDetailsService.loadUserByUsername(user.email)

            // 5. Access Token 및 Refresh Token 발행
            val accessToken = jwtService.generateAccessToken(userDetails)

            // 6. Refresh Token 생성/저장 및 값 가져오기
            val refreshToken = refreshTokenService.createOrUpdateOpaqueRefreshToken(user.id)

            // 7. Access Token을 HttpOnly 쿠키에 담아 전달 (짧은 만료 시간)
            val accessTokenCookie = Cookie(accessTokenCookieName, accessToken).apply {
                isHttpOnly = true
                secure = request.isSecure
                path = accessTokenPath
                maxAge = accessTokenCookieMaxAge
            }
            response.addCookie(accessTokenCookie)

            // 8. Refresh Token을 HttpOnly 쿠키에 담아 전달 (긴 만료 시간)
            val refreshTokenCookie = Cookie(refreshTokenCookieName, refreshToken).apply {
                isHttpOnly = true
                secure = request.isSecure
                path = refreshTokenPath
                maxAge = refreshTokenCookieMaxAge
            }
            response.addCookie(refreshTokenCookie)

            // 9. 성공 처리 및 리디렉션
            clearAuthenticationAttributes(request) // Spring Security가 사용한 임시 세션 데이터 정리
            redirectStrategy.sendRedirect(request, response, frontendRedirectUrl)
            logger.info {"Successfully generated JWT and redirected user ${user.email}"}
        } catch (e: Exception) {
            logger.error("Error during post-authentication processing for user: ${googleUserInfo.email}", e)
            clearTokens(response)
            throw OidcAuthenticationProcessingException("Error during post-authentication processing", e)
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