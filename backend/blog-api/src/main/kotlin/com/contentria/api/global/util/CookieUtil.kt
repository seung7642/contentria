package com.contentria.api.global.util

import com.contentria.api.global.properties.AppProperties
import jakarta.servlet.http.Cookie
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

    fun createAccessTokenCookie(tokenValue: String): Cookie {
        return createCookie(accessTokenCookieName, tokenValue, accessTokenPath, accessTokenMaxAge, true)
    }

    fun createRefreshTokenCookie(tokenValue: String): Cookie {
        return createCookie(refreshTokenCookieName, tokenValue, refreshTokenPath, refreshTokenMaxAge, true)
    }

    fun clearAccessTokenCookie(): Cookie {
        return createCookie(accessTokenCookieName, null, accessTokenPath, 0, true)
    }

    fun clearRefreshTokenCookie(): Cookie {
        return createCookie(refreshTokenCookieName, null, refreshTokenPath, 0, true)
    }

    private fun createCookie(
        name: String,
        value: String?,
        path: String,
        maxAge: Int,
        isHttpOnlyCookie: Boolean
    ): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = isHttpOnlyCookie
            secure = appProperties.auth.cookie.secure
            this.path = path
            this.maxAge = maxAge
            setAttribute("SameSite", "Lax")
        }
    }
}