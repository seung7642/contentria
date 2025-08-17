package com.contentria.api.auth.service

import com.contentria.api.auth.dto.SignUpInitiateRequest
import com.contentria.api.auth.dto.SignUpInitiateResponse
import com.contentria.api.auth.dto.SignUpResponse
import com.contentria.api.auth.dto.VerificationCodeRequest
import com.contentria.api.config.exception.MissingRecaptchaTokenException
import com.contentria.api.config.exception.RecaptchaVerificationFailedException
import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class SignUpServiceImpl(
    private val verificationCodeService: VerificationCodeService,
    private val recaptchaService: RecaptchaService,
    private val userService: UserService,
    private val appProperties: AppProperties,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) : SignUpService {

    private val recaptchaProperties = appProperties.auth.recaptcha

    @Transactional
    override fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest): SignUpInitiateResponse {
        val clientIp = getClientIp(httpRequest) ?: throw IllegalArgumentException("Client IP address not found")

        verifyRecaptcha(request, clientIp)?.let { return it }

        // TODO: 기본 유효성 검사 (예: 이메일 형식, 이메일 계정 존재 유무, 비밀번호
        if (userService.existsByEmail(request.email)) {
            throw RuntimeException("Email already exists")
        }

        // TODO: 인증코드 발송
        if (!verificationCodeService.hasRecentValidCode(request.email)) {
            try {
                verificationCodeService.generateAndSend(request.email, request.name)
            } catch (e: Exception) {
                throw RuntimeException("Failed to send verification code", e)
            }
        }

        return SignUpInitiateResponse("success", "enter_verification_code")
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

    private fun verifyRecaptcha(request: SignUpInitiateRequest, clientIp: String?): SignUpInitiateResponse? {
        val isV2Attempt = !request.recaptchaV2Token.isNullOrBlank()

        if (isV2Attempt) {
            val result = recaptchaService.verifyRecaptchaV2(request.recaptchaV2Token, clientIp).block()
            if (result == null || !result.success) {
                logger.warn { "reCAPTCHA v2 verification failed for email ${request.email}. Details ${result?.details}" }
                throw RecaptchaVerificationFailedException(result?.details)
            }
        } else {
            if (request.recaptchaV3Token.isNullOrBlank()) {
                throw MissingRecaptchaTokenException("v3")
            }

            val result =
                recaptchaService.verifyRecaptchaV3(request.recaptchaV3Token, clientIp, RECAPTCHA_SIGN_UP_ACTION).block()

            if (result == null || !result.success) {
                throw RecaptchaVerificationFailedException(result?.details)
            }

            if (result.score != null && result.score < recaptchaProperties.scoreThreshold) {
                return SignUpInitiateResponse("recaptcha_v2_required", "verify_with_recaptcha_v2")
            }
        }

        return null
    }

    @Transactional
    override fun verifyCode(request: VerificationCodeRequest): SignUpResponse {
        // 1. Verify the verification code
        if (!verificationCodeService.verify(request.email, request.verificationCode)) {
            throw RuntimeException("Invalid verification code")
        }

        // 2. 사용자 계정 생성 또는 업데이트 (미인증 상태에서 인증으로)
//        if (userRepository.existsByEmail(request.email)) {
//            throw RuntimeException("Email already exists")
//        }
//
//        val userRole = roleRepository.findByName(RoleType.USER.name)
//            ?: throw IllegalStateException("User role does not exist")
//
//        val newUser = User(
//            email = request.email,
//            realUsername = request.name,
//            username = request.name,
//            pictureUrl = null,
//            providerId = null,
//        )
//        val savedUser = userRepository.save(newUser)
//
//        // 3. JWT 토큰 발급
//        val userDetails = userService.loadUserByUsername(savedUser.email)
//        val authToken = jwtService.generateToken(userDetails)
//
//        return SignUpResponse(
//            message = "success",
//            authToken = authToken,
//            message = "회원가입이 완료되었습니다.",
//            user = userService.getUserInfo(savedUser.id!!)
//        )
        return null!!
    }

    override fun resendVerificationCode(email: String): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private const val RECAPTCHA_SIGN_UP_ACTION = "signup_initiate"
    }
}