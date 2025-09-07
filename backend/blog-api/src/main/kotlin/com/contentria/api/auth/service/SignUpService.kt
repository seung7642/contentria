package com.contentria.api.auth.service

import com.contentria.api.auth.dto.*
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.controller.UserInfoResponse
import com.contentria.api.user.service.UserService
import com.contentria.api.utils.IpResolver
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
    private val jwtService: JwtService,
    private val ipResolver: IpResolver,
    appProperties: AppProperties,
) {
    private val recaptchaProperties = appProperties.auth.recaptcha

    @Transactional
    fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest): SignUpInitiateResponse {
        val clientIp = ipResolver.getClientIp(httpRequest) ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)

        val result = isValidRecaptchaToken(request, clientIp)
        if (!result) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        userService.createUnverifiedUser(request.email, request.name, request.password)

        verificationCodeService.send(request.email, request.name)

        return SignUpInitiateResponse("success", "enter_verification_code")
    }

    private fun isValidRecaptchaToken(request: SignUpInitiateRequest, clientIp: String?): Boolean {
        if (request.hasRecaptchaV2Token()) {
            val result = recaptchaService.verifyV2(request.recaptchaV2Token!!, clientIp)
            if (!result.success) {
                return false
            }
            return true
        } else if (request.hasRecaptchaV3Token()) {
            val result = recaptchaService.verifyV3(request.recaptchaV3Token!!, clientIp)
            if (!result.success) {
                log.warn { "reCAPTCHA V3 verification failed. Google Errors: ${result.errorCodes}" }
                return false
            }
            if (result.isValidAction(RECAPTCHA_SIGN_UP_ACTION)) {
                log.warn { "reCAPTCHA action mismatch. Expected: $RECAPTCHA_SIGN_UP_ACTION, Got: ${result.action}" }
                return false
            }
            if (result.isHighScore(recaptchaProperties.scoreThreshold)) {
                log.info { "reCAPTCHA score is below threshold. Score: ${result.score}, Threshold: ${recaptchaProperties.scoreThreshold}" }
                return false
            }
            return true
        }

        return false
    }

    @Transactional
    fun verifyCode(request: VerifyCodeRequest): SignUpResponse {
        val result = verificationCodeService.verify(request.email, request.verificationCode)
        if (!result) {
            throw ContentriaException(ErrorCode.INVALID_VERIFICATION_CODE)
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

    @Transactional
    fun login(request: LoginRequest): LoginResponse {
        TODO("Not yet implemented")
    }

    @Transactional
    fun sendOtp(request: SendOtpRequest): SendOtpResponse {
        TODO("Not yet implemented")
    }

    companion object {
        private const val RECAPTCHA_SIGN_UP_ACTION = "signup_initiate"
    }
}