package com.contentria.api.global.security

import com.contentria.api.auth.application.AuthFacade
import com.contentria.api.auth.application.dto.SocialLoginCommand
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.api.global.properties.AppProperties
import com.contentria.api.global.util.CookieUtil
import com.contentria.api.user.domain.AuthProvider
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class CustomOidcAuthenticationSuccessHandler(
    private val authFacade: AuthFacade,
    private val cookieUtil: CookieUtil,
    appProperties: AppProperties,
) : SimpleUrlAuthenticationSuccessHandler() {

    private val frontendRedirectUrl: String = appProperties.auth.oidc.successRedirectUrl

    @Transactional
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        log.debug { "OIDC authentication success handler started" }

        val oidcUser = authentication.principal as? OidcUser
            ?: run {
                log.error {"Authentication principal is not an OidcUser: ${authentication.principal}"}
                throw ContentriaException(ErrorCode.OIDC_INVALID_PRINCIPAL)
            }

        val socialLoginCommand = SocialLoginCommand(
            email = oidcUser.email ?: run {
                log.error { "Email not found in OIDC claims for user: ${oidcUser.subject}" }
                throw ContentriaException(ErrorCode.OIDC_MISSING_EMAIL)
            },
            name = oidcUser.fullName ?: oidcUser.givenName ?: oidcUser.subject,
            picture = oidcUser.picture,
            provider = AuthProvider.GOOGLE,
            providerId = oidcUser.subject,
        )

        try {
            val loginInfo = authFacade.loginWithSocial(socialLoginCommand)

            response.addCookie(cookieUtil.createAccessTokenCookie(loginInfo.accessToken, request))
            response.addCookie(cookieUtil.createRefreshTokenCookie(loginInfo.refreshToken, request))

            clearAuthenticationAttributes(request) // Clear temporary session data used by Spring Security
            SecurityContextHolder.clearContext()
            request.getSession(false)?.invalidate() // OAuth2 로그인 과정에서 생성된 임시 HTTP 세션 무효화

            redirectStrategy.sendRedirect(request, response, frontendRedirectUrl)

            log.debug { "OIDC authentication success handler completed" }
        } catch (e: Exception) {
            log.error(e) { "OIDC post-authentication processing failed: provider=${socialLoginCommand.provider}" }

            response.addCookie(cookieUtil.clearAccessTokenCookie(request))
            response.addCookie(cookieUtil.clearRefreshTokenCookie(request))

            if (e is ContentriaException) {
                throw e
            } else {
                throw ContentriaException(ErrorCode.OIDC_POST_PROCESSING_FAILED)
            }
        }
    }
}