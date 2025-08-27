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
        val authHeader = request.getHeader("Authorization")

        // 1. Authorization 헤더가 없거나 "Bearer " 로 시작하지 않으면 다음 필터로 넘김
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val jwt = authHeader.substring(7)

        try {// 2. 토큰에서 사용자 이메일 추출 (서명 검증 포함)
            val userEmail = jwtService.getUsernameFromToken(jwt)

            // 3. SecurityContext에 인증 정보가 아직 없는 경우에만 처리
            if (SecurityContextHolder.getContext().authentication == null) {
                // 4. DB에서 최신 사용자 정보 로드
                val userDetails = this.customUserDetailsService.loadUserByUsername(userEmail)

                // 5. 토큰이 유효한지 최종 검증 (토큰의 이메일과 DB의 이메일이 일치하는지 확인)
                if (jwtService.isTokenValid(jwt, userDetails.username)) {
                    // 6. 인증 토큰 생성
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                    ).apply {
                        // 요청에 대한 세부 정보 설정
                        details = WebAuthenticationDetailsSource().buildDetails(request)
                    }

                    // 7. SecurityContext에 인증 정보 설정
                    SecurityContextHolder.getContext().authentication = authToken
                    log.debug { "Authenticated user '$userEmail', setting security context." }
                }
            }
        } catch (e: Exception) {
            log.warn { "JWT Token processing failed: ${e.message}" }
            // 실패 시 SecurityContext를 비워두어, 뒤의 인가(Authorization) 단계에서 거부되도록 함
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}