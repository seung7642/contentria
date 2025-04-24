package com.demo.blog.common.security

import com.demo.blog.auth.service.RefreshTokenService
import com.demo.blog.common.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
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

    private val refreshTokenCookieName: String = appProperties.auth.cookie.refreshTokenName

    override fun logout(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val refreshTokenCookie = WebUtils.getCookie(request, refreshTokenCookieName)

        if (refreshTokenCookie?.value != null) {
            try {
                refreshTokenService.deleteRefreshTokenByToken(refreshTokenCookie.value)
                logger.info { "Successfully deleted refresh token from DB during logout." }
            } catch (e: Exception) {
                logger.error(e){ "Error deleting refresh token from DB during logout." }
            }
        }  else {
            logger.warn { "Refresh token cookie not found during logout." }
        }
    }
}