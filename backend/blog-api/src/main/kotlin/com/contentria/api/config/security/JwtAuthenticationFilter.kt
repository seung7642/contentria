package com.contentria.api.config.security

import com.contentria.api.auth.service.JwtService
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
    private val jwtService: JwtService,
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
                authenticateUser(jwt, request)
            }
        } catch (e: Exception) {
            log.warn { "JWT Processing Failed: ${e.message}" }
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        log.debug { "Resolving token with Authorization header." }
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7)
        }

        log.debug { "Resolving token with accessToken cookie." }
        return request.cookies?.find { it.name == "accessToken" }?.value
    }

    private fun authenticateUser(jwt: String, request: HttpServletRequest) {
        val userEmail = jwtService.getUsernameFromToken(jwt)

        val userDetails = this.customUserDetailsService.loadUserByUsername(userEmail)
        if (jwtService.isTokenValid(jwt, userDetails.username)) {
            val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

            SecurityContextHolder.getContext().authentication = authToken
            log.debug { "Authenticated user '$userEmail', setting security context." }
        }
    }
}