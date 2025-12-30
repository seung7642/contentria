package com.contentria.api.auth.service

import com.contentria.api.auth.dto.*
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.dto.CurrentUserResponse
import com.contentria.api.user.service.UserService
import com.contentria.api.utils.IpResolver
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class AuthService(
    private val verificationCodeService: VerificationCodeService,
    private val recaptchaService: RecaptchaService,
    private val userService: UserService,
    private val jwtService: JwtService,
    private val ipResolver: IpResolver,
    private val refreshTokenService: RefreshTokenService,
    private val passwordEncoder: PasswordEncoder,
    appProperties: AppProperties,
) {
    private val recaptchaProperties = appProperties.auth.recaptcha

    @Transactional
    fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest): SignUpInitiateResponse {
        val clientIp = ipResolver.getClientIp(httpRequest) ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)
        if (!isValidRecaptchaToken(request, RECAPTCHA_SIGN_UP_ACTION, clientIp)) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        userService.createUnverifiedUser(request.email, request.name, request.password)

        verificationCodeService.send(request.email, request.name)

        return SignUpInitiateResponse("success")
    }

    @Transactional
    fun verifyCode(request: VerifyCodeRequest): VerifyCodeInfo {
        val result = verificationCodeService.verify(request.email, request.verificationCode)
        if (!result) {
            throw ContentriaException(ErrorCode.INVALID_VERIFICATION_CODE)
        }

        val activatedUser = userService.activateUserByEmail(request.email)

        val accessToken = jwtService.generateAccessToken(activatedUser)
        val refreshToken = jwtService.generateRefreshToken(activatedUser)

        return VerifyCodeInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = CurrentUserResponse.from(activatedUser)
        )
    }

    @Transactional
    fun login(request: LoginRequest, httpRequest: HttpServletRequest): LoginInfo {
        val user = userService.findByEmail(request.email)
            ?: throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)

        val clientIp = ipResolver.getClientIp(httpRequest) ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)
        if (!isValidRecaptchaToken(request, RECAPTCHA_LOGIN_WITH_PASSWORD_ACTION, clientIp)) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)
        }

        if (!user.status.isActive()) {
            throw ContentriaException(ErrorCode.USER_NOT_ACTIVATED)
        }

        val accessToken = jwtService.generateAccessToken(user)
        val refreshToken = refreshTokenService.createOrUpdateOpaqueRefreshToken(user.id!!)

        log.info { "User logged in successfully: ${user.email}" }
        return LoginInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = CurrentUserResponse.from(user)
        )
    }

    @Transactional
    fun sendOtp(request: SendOtpRequest, httpRequest: HttpServletRequest) {
        val user = userService.findByEmail(request.email)
            ?: throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)

        val clientIp = ipResolver.getClientIp(httpRequest) ?: throw ContentriaException(ErrorCode.CLIENT_IP_NOT_FOUND)
        if (!isValidRecaptchaToken(request, RECAPTCHA_SEND_OTP_ACTION, clientIp)) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        if (!user.status.isActive()) {
            throw ContentriaException(ErrorCode.USER_NOT_ACTIVATED)
        }

        verificationCodeService.send(request.email)
    }

    private fun isValidRecaptchaToken(request: RecaptchaRequest, action: String, clientIp: String?): Boolean {
        return if (request.hasRecaptchaV2Token()) {
            recaptchaService.isV2TokenValid(request.recaptchaV2Token!!, clientIp)
        } else if (request.hasRecaptchaV3Token()) {
            recaptchaService.isV3TokenValid(
                request.recaptchaV3Token!!,
                clientIp,
                action,
                recaptchaProperties.scoreThreshold
            )
        } else {
            false
        }
    }

    companion object {
        private const val RECAPTCHA_SIGN_UP_ACTION = "signup_initiate"
        private const val RECAPTCHA_LOGIN_WITH_PASSWORD_ACTION = "login_with_password"
        private const val RECAPTCHA_SEND_OTP_ACTION = "send_otp_code"
    }
}