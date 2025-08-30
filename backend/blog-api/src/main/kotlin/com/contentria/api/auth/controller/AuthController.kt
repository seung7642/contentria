package com.contentria.api.auth.controller

import com.contentria.api.auth.dto.SignUpInitiateRequest
import com.contentria.api.auth.dto.SignUpInitiateResponse
import com.contentria.api.auth.dto.SignUpResponse
import com.contentria.api.auth.dto.VerifyCodeRequest
import com.contentria.api.auth.service.RefreshTokenService
import com.contentria.api.auth.service.SignUpService
import com.contentria.api.utils.CookieUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/auth")
class AuthController(
    private val refreshTokenService: RefreshTokenService,
    private val signUpService: SignUpService,
    private val cookieUtil: CookieUtil
) {

    @PostMapping("/refresh")
    fun refreshToken(
        @CookieValue(name = "\${app.auth.cookie.refresh-token-name}", required = true) refreshTokenValue: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        val newTokens = refreshTokenService.refreshTokens(refreshTokenValue)

        response.addCookie(cookieUtil.createAccessTokenCookie(newTokens.accessToken, request))
        response.addCookie(cookieUtil.createRefreshTokenCookie(newTokens.refreshToken, request))

        logger.info { "Successfully refreshed token." }
        return ResponseEntity.ok().body(mapOf("message" to "Token refreshed successfully"))
    }

    @PostMapping("/signup/initiate")
    fun initiateSignUp(
        @Valid @RequestBody request: SignUpInitiateRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<SignUpInitiateResponse> {
        val response = signUpService.initiate(request, httpRequest)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/signup/verify-code")
    fun verifyCode(@Valid @RequestBody request: VerifyCodeRequest): ResponseEntity<SignUpResponse> {
        val response = signUpService.verifyCode(request)
        return ResponseEntity.ok(response)
    }
}