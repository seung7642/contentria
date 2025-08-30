package com.contentria.api.config.security

import com.contentria.api.auth.service.JwtService
import com.contentria.api.auth.service.RefreshTokenService
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.domain.User
import com.contentria.api.user.security.GoogleUserInfo
import com.contentria.api.user.service.UserService
import com.contentria.api.utils.CookieUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Component
class CustomOidcAuthenticationSuccessHandler(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
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
        log.info { "OIDC authentication success handler started." }

        val oidcUser = authentication.principal as? OidcUser
            ?: run {
                log.error {"Authentication principal is not an OidcUser: ${authentication.principal}"}
                throw ContentriaException(ErrorCode.OIDC_INVALID_PRINCIPAL)
            }

        val googleUserInfo = GoogleUserInfo(
            id = oidcUser.subject,
            email = oidcUser.email ?: run {
                log.error { "Email not found in OIDC claims for user: ${oidcUser.subject}" }
                throw ContentriaException(ErrorCode.OIDC_MISSING_EMAIL)
            },
            name = oidcUser.fullName ?: oidcUser.givenName ?: oidcUser.subject,
            picture = oidcUser.picture
        )

        try {
            val user: User = userService.upsertGoogleUser(googleUserInfo)
            val accessToken = jwtService.generateAccessToken(user)
            val refreshToken = refreshTokenService.createOrUpdateOpaqueRefreshToken(user.id)

            response.addCookie(cookieUtil.createAccessTokenCookie(accessToken, request))
            response.addCookie(cookieUtil.createRefreshTokenCookie(refreshToken, request))

            clearAuthenticationAttributes(request) // Clear temporary session data used by Spring Security
            redirectStrategy.sendRedirect(request, response, frontendRedirectUrl)

            log.info { "OIDC authentication success handler ended." }
        } catch (e: Exception) {
            log.error(e) { "Error during post-authentication processing for user: ${googleUserInfo.email}" }

            response.addCookie(cookieUtil.clearAccessTokenCookie(request))
            response.addCookie(cookieUtil.clearRefreshTokenCookie(request))

            if (e is ContentriaException) throw e
            else throw ContentriaException(ErrorCode.OIDC_POST_PROCESSING_FAILED)
        }
    }
}