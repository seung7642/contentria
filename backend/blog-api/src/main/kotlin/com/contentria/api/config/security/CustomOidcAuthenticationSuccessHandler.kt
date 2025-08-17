package com.contentria.api.config.security

import com.contentria.api.auth.service.JwtService
import com.contentria.api.auth.service.RefreshTokenService
import com.contentria.api.config.exception.OidcAuthenticationProcessingException
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.domain.User
import com.contentria.api.user.security.CustomUserDetailsService
import com.contentria.api.user.security.GoogleUserInfo
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Component
class CustomOidcAuthenticationSuccessHandler(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val appProperties: AppProperties,
    private val customUserDetailsService: CustomUserDetailsService,
    private val refreshTokenService: RefreshTokenService
) : SimpleUrlAuthenticationSuccessHandler() {

    private val accessTokenCookieMaxAge: Int = appProperties.auth.jwt.accessTokenExpiration.toSeconds().toInt()
    private val refreshTokenCookieMaxAge: Int = appProperties.auth.jwt.refreshTokenExpiration.toSeconds().toInt()
    private val accessTokenCookieName: String = appProperties.auth.cookie.accessTokenName
    private val refreshTokenCookieName: String = appProperties.auth.cookie.refreshTokenName
    private val accessTokenPath: String = appProperties.auth.cookie.accessTokenPath
    private val refreshTokenPath: String = appProperties.auth.cookie.refreshTokenPath
    private val frontendRedirectUrl: String = appProperties.auth.oidc.successRedirectUrl

    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        logger.info { "OIDC authentication success handler started." }

        val oidcUser = authentication.principal as? OidcUser
            ?: run {
                logger.error {"Authentication principal is not an OidcUser: ${authentication.principal}"}
                throw OidcAuthenticationProcessingException("Invalid user type")
            }

        val googleUserInfo = GoogleUserInfo(
            id = oidcUser.subject,
            email = oidcUser.email ?: run {
                logger.error { "Email not found in OIDC claims for user: ${oidcUser.subject}" }
                throw OidcAuthenticationProcessingException("Email not found in OIDC claims")
            },
            name = oidcUser.fullName ?: oidcUser.givenName ?: oidcUser.subject,
            picture = oidcUser.picture
        )

        try {
            val user: User = userService.upsertGoogleUser(googleUserInfo)
            val userDetails = customUserDetailsService.loadUserByUsername(user.email)

            val accessToken = jwtService.generateAccessToken(userDetails)
            val refreshToken = refreshTokenService.createOrUpdateOpaqueRefreshToken(user.id)

            response.addCookie(
                createCookie(
                    name = accessTokenCookieName,
                    value = accessToken,
                    path = accessTokenPath,
                    maxAge = accessTokenCookieMaxAge,
                    request = request
                )
            )

            response.addCookie(
                createCookie(
                    name = refreshTokenCookieName,
                    value = refreshToken,
                    path = refreshTokenPath,
                    maxAge = refreshTokenCookieMaxAge,
                    request = request
                )
            )

            clearAuthenticationAttributes(request) // Clear temporary session data used by Spring Security
            redirectStrategy.sendRedirect(request, response, frontendRedirectUrl)

            logger.info { "OIDC authentication success handler ended." }
        } catch (e: Exception) {
            logger.error("Error during post-authentication processing for user: ${googleUserInfo.email}", e)
            clearTokens(response, request)
            throw OidcAuthenticationProcessingException("Error during post-authentication processing", e)
        }
    }

    private fun createCookie(
        name: String,
        value: String?,
        path: String,
        maxAge: Int,
        request: HttpServletRequest
    ): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = true
            secure = request.isSecure
            this.path = path
            this.maxAge = maxAge
        }
    }

    private fun clearTokens(response: HttpServletResponse, request: HttpServletRequest) {
        response.addCookie(
            createCookie(accessTokenCookieName, null, accessTokenPath, 0, request)
        )
        response.addCookie(
            createCookie(refreshTokenCookieName, null, refreshTokenPath, 0, request)
        )
    }
}