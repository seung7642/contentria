package com.contentria.api.auth.service

import com.contentria.api.auth.dto.*
import com.contentria.api.config.exception.MissingRecaptchaTokenException
import com.contentria.api.config.exception.RecaptchaVerificationFailedException
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

        // 1. reCAPTCHA 검증 결과를 '해석'하여 명확한 상태(Success, V2Required, Failure)로 받는다.
        val recaptchaResult = interpretRecaptchaForSignUp(request, clientIp)

        // 2. 'when' 구문을 사용하여 검증 상태에 따라 명확하게 분기 처리한다.
        when (recaptchaResult) {
            is RecaptchaSignUpResult.Success -> {
                log.info { "reCAPTCHA verification successful for email: ${request.email}" }
            }
            is RecaptchaSignUpResult.V2Required -> {
                return SignUpInitiateResponse("recaptcha_v2_required", "verify_with_recaptcha_v2")
            }
            is RecaptchaSignUpResult.Failure -> {
                log.warn { "reCAPTCHA verification failed for email ${request.email}. Reason: ${recaptchaResult.reason}, Details: ${recaptchaResult.googleErrorCodes}" }
                throw RecaptchaVerificationFailedException(recaptchaResult.reason)
            }
        }

        // 3. 아직 인증되지 않은 사용자를 생성한다.
        userService.createUnverifiedUser(request.email, request.name, request.password)

        // 4. 이메일로 인증 코드를 발송한다.
        verificationCodeService.send(request.email, request.name)

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

//    private fun verifyRecaptcha(request: SignUpInitiateRequest, clientIp: String?): SignUpInitiateResponse? {
//        val isV2Attempt = !request.recaptchaV2Token.isNullOrBlank()
//
//        if (isV2Attempt) {
//            val result = recaptchaService.verifyRecaptchaV2(request.recaptchaV2Token, clientIp).block()
//            if (result == null || !result.success) {
//                log.warn { "reCAPTCHA v2 verification failed for email ${request.email}. Details ${result?.details}" }
//                throw RecaptchaVerificationFailedException(result?.details)
//            }
//        } else {
//            if (request.recaptchaV3Token.isNullOrBlank()) {
//                throw MissingRecaptchaTokenException("v3")
//            }
//
//            val result =
//                recaptchaService.verifyRecaptchaV3(request.recaptchaV3Token, clientIp, RECAPTCHA_SIGN_UP_ACTION).block()
//
//            if (result == null || !result.success) {
//                throw RecaptchaVerificationFailedException(result?.details)
//            }
//
//            if (result.score != null && result.score < recaptchaProperties.scoreThreshold) {
//                return SignUpInitiateResponse("recaptcha_v2_required", "verify_with_recaptcha_v2")
//            }
//        }
//
//        return null
//    }

    /**
     * RecaptchaService로부터 받은 순수 데이터를 '회원가입 정책'에 맞게 해석(Interpret)합니다.
     */
    private fun interpretRecaptchaForSignUp(request: SignUpInitiateRequest, clientIp: String?): RecaptchaSignUpResult {
        // V2 토큰이 있다면 V2 검증을 우선적으로 시도한다.
        if (!request.recaptchaV2Token.isNullOrBlank()) {
            // 1. RecaptchaService 호출 (순수한 데이터 받기)
            val response = recaptchaService.verifyV2(request.recaptchaV2Token!!, clientIp).block()

            // 2. 비즈니스 로직 적용 (결과 해석)
            return if (response?.success == true) {
                RecaptchaSignUpResult.Success
            } else {
                RecaptchaSignUpResult.Failure("reCAPTCHA V2 verification failed", response?.errorCodes)
            }
        }

        // V2 토큰이 없을 경우, V3 토큰이 반드시 있어야 한다.
        if (request.recaptchaV3Token.isNullOrBlank()) {
            throw MissingRecaptchaTokenException("reCAPTCHA V3 token is missing")
        }

        // V3 토큰을 검증한다.
        val response = recaptchaService.verifyV3(request.recaptchaV3Token, clientIp).block()

        // 1. Google API의 응답 자체가 실패인 경우
        if (response?.success != true) {
            return RecaptchaSignUpResult.Failure("Google API returned failure", response?.errorCodes)
        }

        // 2. Action 이름이 일치하지 않는 경우 (재전송 공격 방지)
        if (response.action != RECAPTCHA_SIGN_UP_ACTION) {
            log.warn { "reCAPTCHA action mismatch. Expected: $RECAPTCHA_SIGN_UP_ACTION, Got: ${response.action}" }
            return RecaptchaSignUpResult.Failure("Action mismatch")
        }

        // 3. 점수가 임계값 미만인 경우 (봇으로 의심될 경우)
        if (response.score != null && response.score < recaptchaProperties.scoreThreshold) {
            log.info { "reCAPTCHA score is below threshold. Score: ${response.score}, Threshold: ${recaptchaProperties.scoreThreshold}" }
            return RecaptchaSignUpResult.V2Required
        }

        // 모든 검증을 통과한 경우
        return RecaptchaSignUpResult.Success
    }

    @Transactional
    fun verifyCode(request: VerifyCodeRequest): SignUpResponse {
        // 1. 코드 검증 (실패 시 여기서 예외 발생하며 중단됨)
        verificationCodeService.verify(request.email, request.verificationCode)

        // 2. 사용자 활성화 (실패 시 여기서 예외 발생하며 중단됨)
        val activatedUser = userService.activateUserByEmail(request.email)

        // 3. JWT 토큰 발급 (자동 로그인을 위해)
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