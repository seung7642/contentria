package com.contentria.api.auth.controller

import com.contentria.api.auth.application.AuthFacade
import com.contentria.api.auth.controller.dto.*
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.global.util.CookieUtil
import com.contentria.api.global.util.IpResolver
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
    private val authFacade: AuthFacade,
    private val cookieUtil: CookieUtil,
    private val ipResolver: IpResolver
) {

    @ApiLog
    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue(name = "\${app.auth.cookie.refresh-token-name}", required = true) refreshTokenValue: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<RefreshTokenResponse> {
        log.info { "=== Refresh with $refreshTokenValue ===" }
        val newTokens = authFacade.refreshTokens(refreshTokenValue)

        response.addCookie(cookieUtil.createAccessTokenCookie(newTokens.accessToken, request))
        response.addCookie(cookieUtil.createRefreshTokenCookie(newTokens.refreshToken, request))

        log.info { "=== Successfully refreshed token. accessToken:${newTokens.accessToken.substring(0, 10)}, refreshToken:${newTokens.refreshToken.substring(0, 10)}" }
        return ResponseEntity.ok(
            RefreshTokenResponse(
                accessToken = newTokens.accessToken,
                refreshToken = newTokens.refreshToken
            )
        )
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<LoginResponse> {
        val clientIp = ipResolver.getClientIp(httpRequest)
            ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)

        val command = request.toCommand(clientIp)
        val result = authFacade.login(command)

        httpResponse.addCookie(cookieUtil.createAccessTokenCookie(result.accessToken, httpRequest))
        httpResponse.addCookie(cookieUtil.createRefreshTokenCookie(result.refreshToken, httpRequest))

        return ResponseEntity.ok(LoginResponse.from(result.user, result.accessToken, result.refreshToken))
    }

    @PostMapping("/signup/initiate")
    fun initiateSignUp(
        @Valid @RequestBody request: SignUpInitiateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Unit> {
        val clientIp = ipResolver.getClientIp(httpRequest)
            ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)

        val command = request.toCommand(clientIp)
        authFacade.initiate(command)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/verify-code")
    fun verifyCode(
        @Valid @RequestBody request: VerifyCodeRequest,
        httpRequest: HttpServletRequest,
        httpResponse: HttpServletResponse
    ): ResponseEntity<VerifyCodeResponse> {
        val verifyCodeInfo = authFacade.verifyCode(request.toCommand())

        httpResponse.addCookie(cookieUtil.createAccessTokenCookie(verifyCodeInfo.accessToken, httpRequest))
        httpResponse.addCookie(cookieUtil.createRefreshTokenCookie(verifyCodeInfo.refreshToken, httpRequest))

        return ResponseEntity.ok(VerifyCodeResponse.from(verifyCodeInfo))
    }

    @PostMapping("/send-otp")
    fun sendOtp(@Valid @RequestBody request: SendOtpRequest, httpRequest: HttpServletRequest): ResponseEntity<Unit> {
        val clientIp = ipResolver.getClientIp(httpRequest)
            ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)

        val command = request.toCommand(clientIp)
        authFacade.sendOtp(command)
        return ResponseEntity.noContent().build()
    }
}