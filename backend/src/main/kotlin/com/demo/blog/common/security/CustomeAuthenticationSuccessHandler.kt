package com.demo.blog.common.security

import com.demo.blog.user.domain.User
import com.demo.blog.user.security.CustomUserDetailsService
import com.demo.blog.user.security.GoogleUserInfo
import com.demo.blog.user.service.UserService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.io.IOException

@Component
class CustomeAuthenticationSuccessHandler(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val customUserDetailsService: CustomUserDetailsService
) : SimpleUrlAuthenticationSuccessHandler() {

    init {
        super.setDefaultTargetUrl("/dashboard")
        super.setTargetUrlParameter(null)
        super.setUseReferer(false)
    }

    @Transactional
    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 1. OIDC 사용자 정보 가져오기
        val oidcUser = authentication.principal as? OidcUser
            ?: run {
                logger.error {"Authentication principal is not an OidcUser: ${authentication.principal}"}
                handleFailure(request, response, "InvalidUserType")
                return
            }

        // 2. GoogleUserInfo 객체 생성 (ID 토큰 클레임 사용)
        val googleUserInfo = GoogleUserInfo(
            id = oidcUser.subject,
            email = oidcUser.email ?: run {
                logger.error { "Email not found in OIDC claims for user: ${oidcUser.subject}" }
                handleFailure(request, response, "EmailNotFound")
                return
            },
            name = oidcUser.fullName ?: oidcUser.givenName ?: oidcUser.subject,
            picture = oidcUser.picture
        )
        logger.info {"Processing OIDC login for user: ${googleUserInfo.email}"}

        try {
            // 3. 사용자 정보 데이터베이스에 저장 또는 업데이트
            val user: User = userService.upsertGoogleUser(googleUserInfo)

            // 4. 자체 JWT 생성을 위해 UserDetails 로드 (데이터베이스에 저장된 역할 등 최신 정보 반영을 위함)
            val userDetails = customUserDetailsService.loadUserByUsername(user.email)

            // 5. 자체 JWT 토큰 발행
            val jwtToken = jwtService.generateToken(userDetails)

            // 6. JWT 토큰을 HttpOnly 쿠키에 담아 전달
            val cookie = Cookie("auth_token", jwtToken).apply {
                isHttpOnly = true // JavaScript에서 접근 불가
                secure = request.isSecure // HTTPS에서만 전송
                path = "/"
                maxAge = 24 * 60 * 60 // 쿠키 유효 기간 (1일)
            }
            response.addCookie(cookie)

            // 7. 성공 처리 및 리디렉션
            // Spring Security가 사용한 임시 세션 데이터 정리
            clearAuthenticationAttributes(request)
            val redirectUrl = "/dashboard"
            redirectStrategy.sendRedirect(request, response, redirectUrl)
            logger.info {"Successfully generated JWT and redirected user ${user.email}"}
        } catch (e: Exception) {
            logger.error("Error during post-authentication processing for user: ${googleUserInfo.email}", e)
            handleFailure(request, response, "ProcessingError", e)
        }
    }

    private fun handleFailure(request: HttpServletRequest, response: HttpServletResponse, errorCode: String, exception: Exception? = null) {
        clearAuthenticationAttributes(request) // 실패 시 세션 정리 시도
        val errorRedirectUrl = "/error?code=$errorCode"
        try {
            redirectStrategy.sendRedirect(request, response, errorRedirectUrl)
        } catch (e: IOException) {
            logger.error("Failed to redirect to error page: $errorRedirectUrl", e)
        }
    }
}