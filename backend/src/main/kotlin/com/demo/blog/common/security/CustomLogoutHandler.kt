package com.demo.blog.common.security

import com.demo.blog.auth.service.RefreshTokenService
import com.demo.blog.common.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.WebUtils

private val logger = KotlinLogging.logger {}

@Component
class CustomLogoutHandler(
    private val refreshTokenService: RefreshTokenService,
    private val appProperties: AppProperties
) : LogoutHandler {

    private val accessTokenCookieName: String = appProperties.auth.cookie.accessTokenName
    private val refreshTokenCookieName: String = appProperties.auth.cookie.refreshTokenName
    private val accessTokenCookiePath: String = appProperties.auth.cookie.accessTokenPath
    private val refreshTokenCookiePath: String = appProperties.auth.cookie.refreshTokenPath

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        clearTokens(response, request)

        WebUtils.getCookie(request, refreshTokenCookieName)?.value?.let { tokenValue ->
            try {
                refreshTokenService.deleteRefreshTokenByToken(tokenValue)
                logger.info { "Successfully deleted refresh token from DB during logout." }
            } catch (e: Exception) {
                logger.error(e){ "Error deleting refresh token from DB during logout." }
            }
        } ?: run {
            logger.warn { "Refresh token cookie not found during logout." }
        }
    }

    private fun clearTokens(response: HttpServletResponse, request: HttpServletRequest) {
        response.addCookie(
            createCookie(accessTokenCookieName, null, accessTokenCookiePath, 0, request)
        )
        response.addCookie(
            createCookie(refreshTokenCookieName, null, refreshTokenCookiePath, 0, request)
        )
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
}