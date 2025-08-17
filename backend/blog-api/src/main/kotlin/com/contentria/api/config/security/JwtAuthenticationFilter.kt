package com.contentria.api.config.security

import com.contentria.api.auth.service.JwtService
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.security.CustomUserDetailsService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService,
    private val appProperties: AppProperties
) : OncePerRequestFilter() {

    private val accessTokenCookieName = appProperties.auth.cookie.accessTokenName

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            getAccessToken(request)?.takeIf { it.isNotBlank() }?.let { token ->
                if (jwtService.validateToken(token)) {
                    val username = jwtService.getUsernameFromJWT(token)
                    val userDetails = customUserDetailsService.loadUserByUsername(username)
                    val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }
                    SecurityContextHolder.getContext().authentication = authentication
                    logger.debug {"Successfully authenticated user '$username' from JWT."}
                } else {
                    logger.debug { "Invalid or expired JWT received." }
                }
            }
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }

        filterChain.doFilter(request, response)
    }

    private fun getAccessToken(request: HttpServletRequest): String? {
        // 1. Find access token from cookies
        WebUtils.getCookie(request, accessTokenCookieName)
            ?.value
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }

        // 2. Find Bearer token from the Authorization header
        request.getHeader(AUTHORIZATION_HEADER)
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.drop(7)
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }

        return null
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}