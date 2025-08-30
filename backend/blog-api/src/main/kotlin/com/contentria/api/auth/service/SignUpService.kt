package com.contentria.api.auth.service

import com.contentria.api.auth.dto.*
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.controller.UserInfoResponse
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class SignUpService(
    private val verificationCodeService: VerificationCodeService,
    private val recaptchaService: RecaptchaService,
    private val userService: UserService,
    private val appProperties: AppProperties,
    private val jwtService: JwtService
) {

    private val recaptchaProperties = appProperties.auth.recaptcha

    @Transactional
    fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest): SignUpInitiateResponse {
        val clientIp = getClientIp(httpRequest) ?: throw IllegalArgumentException("Client IP address not found")

        when (interpretRecaptchaForSignUp(request, clientIp)) {
            RecaptchaSignUpResult.PROCEED -> {
                log.info { "reCAPTCHA verification successful for email: ${request.email}" }
            }
            RecaptchaSignUpResult.REQUIRE_V2_CHALLENGE -> {
                return SignUpInitiateResponse("recaptcha_v2_required", "verify_with_recaptcha_v2")
            }
        }

        userService.createUnverifiedUser(request.email, request.name, request.password)

        verificationCodeService.send(request.email, request.name)

        return SignUpInitiateResponse("success", "enter_verification_code")
    }

    private fun interpretRecaptchaForSignUp(request: SignUpInitiateRequest, clientIp: String?): RecaptchaSignUpResult {
        if (request.hasRecaptchaV2Token()) {
            val response = recaptchaService.verifyV2(request.recaptchaV2Token!!, clientIp).block()
            return if (response.success) {
                RecaptchaSignUpResult.PROCEED
            } else {
                log.warn { "reCAPTCHA V2 verification failed. Google Errors: ${response.errorCodes}" }
                throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
            }
        }

        if (!request.hasRecaptchaV3Token()) {
            throw ContentriaException(ErrorCode.RECAPTCHA_TOKEN_MISSING)
        }

        val response = recaptchaService.verifyV3(request.recaptchaV3Token!!, clientIp).block()
        if (!response.success) {
            log.warn { "reCAPTCHA V3 verification failed. Google Errors: ${response.errorCodes}" }
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        if (response.action != RECAPTCHA_SIGN_UP_ACTION) {
            log.warn { "reCAPTCHA action mismatch. Expected: $RECAPTCHA_SIGN_UP_ACTION, Got: ${response.action}" }
            throw ContentriaException(ErrorCode.RECAPTCHA_ACTION_MISMATCH)
        }

        if (response.score != null && response.score < recaptchaProperties.scoreThreshold) {
            log.info { "reCAPTCHA score is below threshold. Score: ${response.score}, Threshold: ${recaptchaProperties.scoreThreshold}" }
            return RecaptchaSignUpResult.REQUIRE_V2_CHALLENGE
        }

        return RecaptchaSignUpResult.PROCEED
    }

    private fun getClientIp(request: HttpServletRequest): String? {
        var ipAddress = request.getHeader("X-Forwarded-For")
        if (ipAddress.isNullOrBlank() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("Proxy-Client-IP")
        }
        if (ipAddress.isNullOrBlank() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ipAddress.isNullOrBlank() || "unknown".equals(ipAddress, ignoreCase = true)) {
            ipAddress = request.remoteAddr
        }
        // X-Forwarded-For 헤더는 여러 IP가 콤마로 구분되어 올 수 있으므로 첫 번째 IP를 사용
        return ipAddress?.split(",")?.firstOrNull()?.trim()
    }

    @Transactional
    fun verifyCode(request: VerifyCodeRequest): SignUpResponse {
        val result = verificationCodeService.verify(request.email, request.verificationCode)
        if (!result) {
            throw IllegalArgumentException("Invalid or expired verification code")
        }

        val activatedUser = userService.activateUserByEmail(request.email)

        val accessToken = jwtService.generateAccessToken(activatedUser)
        val refreshToken = jwtService.generateRefreshToken(activatedUser)

        return SignUpResponse(
            message = "signup_complete",
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = UserInfoResponse.from(activatedUser)
        )
    }

    companion object {
        private const val RECAPTCHA_SIGN_UP_ACTION = "signup_initiate"
    }
}