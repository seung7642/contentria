package com.contentria.api.auth.controller

import com.contentria.api.auth.dto.*
import com.contentria.api.auth.service.AuthService
import com.contentria.api.auth.service.RefreshTokenService
import com.contentria.api.utils.CookieUtil
import com.contentria.common.aop.ApiLog
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/auth")
class AuthController(
    private val refreshTokenService: RefreshTokenService,
    private val authService: AuthService,
    private val cookieUtil: CookieUtil
) {

    @ApiLog
    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue(name = "\${app.auth.cookie.refresh-token-name}", required = true) refreshTokenValue: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<RefreshTokenResponse> {
        log.info { "=== Refresh with $refreshTokenValue ===" }
        val newTokens = refreshTokenService.refreshTokens(refreshTokenValue)

        response.addCookie(cookieUtil.createAccessTokenCookie(newTokens.accessToken, request))
        response.addCookie(cookieUtil.createRefreshTokenCookie(newTokens.refreshToken, request))

        log.info { "=== Successfully refreshed token. accessToken:${newTokens.accessToken.substring(0, 10)}, refreshToken:${newTokens.refreshToken.substring(0, 10)}" }
        return ResponseEntity.ok(RefreshTokenResponse(
            accessToken = newTokens.accessToken,
            refreshToken = newTokens.refreshToken
        ))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<LoginResponse> {
        val result = authService.login(request, httpRequest)

        httpResponse.addCookie(cookieUtil.createAccessTokenCookie(result.accessToken, httpRequest))
        httpResponse.addCookie(cookieUtil.createRefreshTokenCookie(result.refreshToken, httpRequest))

        return ResponseEntity.ok(LoginResponse(
            user = result.user,
            accessToken = result.accessToken,
            refreshToken = result.refreshToken
        ))
    }

    @PostMapping("/signup/initiate")
    fun initiateSignUp(
        @Valid @RequestBody request: SignUpInitiateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<SignUpInitiateResponse> {
        val response = authService.initiate(request, httpRequest)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify-code")
    fun verifyCode(
        @Valid @RequestBody request: VerifyCodeRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<VerifyCodeResponse> {
        val result = authService.verifyCode(request)

        httpResponse.addCookie(cookieUtil.createAccessTokenCookie(result.accessToken, httpRequest))
        httpResponse.addCookie(cookieUtil.createRefreshTokenCookie(result.refreshToken, httpRequest))

        return ResponseEntity.ok(VerifyCodeResponse(result.user))
    }

    @PostMapping("/send-otp")
    fun sendOtp(@Valid @RequestBody request: SendOtpRequest, httpRequest: HttpServletRequest): ResponseEntity<Unit> {
        authService.sendOtp(request, httpRequest)
        return ResponseEntity.noContent().build()
    }
}