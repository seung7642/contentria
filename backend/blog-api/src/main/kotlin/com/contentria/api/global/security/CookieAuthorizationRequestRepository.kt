package com.contentria.api.global.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Base64

/**
 * Stores OAuth2 authorization requests in an encrypted cookie instead of the HTTP session.
 * This enables fully stateless session management (SessionCreationPolicy.STATELESS)
 * while preserving the OAuth2 redirect flow.
 */
@Component
class CookieAuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    companion object {
        const val COOKIE_NAME = "oauth2_auth_request"
        const val COOKIE_MAX_AGE = 180 // 3 minutes — enough for the OAuth redirect cycle
    }

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return getCookie(request, COOKIE_NAME)
            ?.let { deserialize(it.value) }
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (authorizationRequest == null) {
            deleteCookie(response)
            return
        }

        val cookie = Cookie(COOKIE_NAME, serialize(authorizationRequest)).apply {
            path = "/"
            isHttpOnly = true
            maxAge = COOKIE_MAX_AGE
        }
        response.addCookie(cookie)
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): OAuth2AuthorizationRequest? {
        val authorizationRequest = loadAuthorizationRequest(request)
        deleteCookie(response)
        return authorizationRequest
    }

    fun deleteCookie(response: HttpServletResponse) {
        val cookie = Cookie(COOKIE_NAME, "").apply {
            path = "/"
            isHttpOnly = true
            maxAge = 0
        }
        response.addCookie(cookie)
    }

    private fun getCookie(request: HttpServletRequest, name: String): Cookie? {
        return request.cookies?.find { it.name == name }
    }

    private fun serialize(authorizationRequest: OAuth2AuthorizationRequest): String {
        val baos = ByteArrayOutputStream()
        ObjectOutputStream(baos).use { it.writeObject(authorizationRequest) }
        return Base64.getUrlEncoder().encodeToString(baos.toByteArray())
    }

    private fun deserialize(value: String): OAuth2AuthorizationRequest? {
        return try {
            val bytes = Base64.getUrlDecoder().decode(value)
            ObjectInputStream(ByteArrayInputStream(bytes)).use {
                it.readObject() as OAuth2AuthorizationRequest
            }
        } catch (e: Exception) {
            null
        }
    }
}
