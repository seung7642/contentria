package com.contentria.api.global.util

import com.contentria.api.global.properties.AppProperties
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class CookieUtil(
    val appProperties: AppProperties
) {
    private val accessTokenCookieName: String get() = appProperties.auth.cookie.accessTokenName
    private val refreshTokenCookieName: String get() = appProperties.auth.cookie.refreshTokenName
    private val accessTokenPath: String get() = appProperties.auth.cookie.accessTokenPath
    private val refreshTokenPath: String get() = appProperties.auth.cookie.refreshTokenPath
    private val accessTokenMaxAge: Int get() = appProperties.auth.jwt.accessTokenExpiration.toSeconds().toInt()
    private val refreshTokenMaxAge: Int get() = appProperties.auth.jwt.refreshTokenExpiration.toSeconds().toInt()

    fun createAccessTokenCookie(tokenValue: String, request: HttpServletRequest): Cookie {
        return createCookie(accessTokenCookieName, tokenValue, accessTokenPath, accessTokenMaxAge, request, true)
    }

    fun createRefreshTokenCookie(tokenValue: String, request: HttpServletRequest): Cookie {
        return createCookie(refreshTokenCookieName, tokenValue, refreshTokenPath, refreshTokenMaxAge, request, true)
    }

    fun clearAccessTokenCookie(request: HttpServletRequest): Cookie {
        return createCookie(accessTokenCookieName, null, accessTokenPath, 0, request, true)
    }

    fun clearRefreshTokenCookie(request: HttpServletRequest): Cookie {
        return createCookie(refreshTokenCookieName, null, refreshTokenPath, 0, request, true)
    }

    private fun createCookie(
        name: String,
        value: String?,
        path: String,
        maxAge: Int,
        request: HttpServletRequest,
        isHttpOnlyCookie: Boolean
    ): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = isHttpOnlyCookie
//            secure = request.isSecure // HTTPS 에서만 쿠키 전송
            this.path = path
            this.maxAge = maxAge
        }
    }
}