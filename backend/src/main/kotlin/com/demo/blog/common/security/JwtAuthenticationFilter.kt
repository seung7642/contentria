package com.demo.blog.common.security

import com.demo.blog.user.security.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    private val jwtCookieName = "auth_token"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt: String? = getJwtFromRequest(request)

            if (!jwt.isNullOrBlank() && jwtService.validateToken(jwt)) {
                val username = jwtService.getUsernameFromJWT(jwt)
                val userDetails = customUserDetailsService.loadUserByUsername(username)

                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                // 부가 정보 설정 (IP 등)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
                logger.debug {"Successfully authenticated user '$username' from JWT."}
            } else {
                if (!jwt.isNullOrBlank()) {
                    logger.debug {"Invalid or expired JWT received."}
                }
            }
        } catch (ex: Exception) {
            logger.error("Could not set user authentication in security context", ex)
        }

        filterChain.doFilter(request, response)
    }

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        // 1. 쿠키에서 JWT 토큰 찾기 (우선 순위)
        val cookie: Cookie? = WebUtils.getCookie(request, jwtCookieName)
        if (cookie != null && StringUtils.hasText(cookie.value)) {
            return cookie.value
        }

        // 2. Authorization 헤더에서 Bearer 토큰 찾기 (당장은 쿠키 방식만 사용 예정)
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }

        return null
    }
}