package com.contentria.api.global.security

import com.contentria.api.auth.application.TokenGenerator
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

private val log = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val tokenGenerator: TokenGenerator,
    private val customUserDetailsService: CustomUserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.debug { "Start JwtAuthenticationFilter..." }

        try {
            val jwt = resolveToken(request)

            if (jwt != null && SecurityContextHolder.getContext().authentication == null) {
                if (tokenGenerator.validateToken(jwt)) {
                    authenticateUser(jwt, request)
                } else {
                    log.warn { "Invalid JWT Token" }
                }
            }
        } catch (e: Exception) {
            log.warn { "JWT Processing Failed: ${e.message}" }
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug { "JWT Found Authorization" }
            return authHeader.substring(7)
        }

        return request.cookies?.find { it.name == "accessToken" }?.value
    }

    private fun authenticateUser(jwt: String, request: HttpServletRequest) {
        val userEmail = tokenGenerator.extractSubject(jwt)
        val userDetails = this.customUserDetailsService.loadUserByUsername(userEmail)

        val authToken = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authToken
        log.debug { "Authenticated user '$userEmail', setting security context." }
    }
}