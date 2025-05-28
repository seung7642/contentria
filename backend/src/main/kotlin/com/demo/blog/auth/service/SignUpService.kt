package com.demo.blog.auth.service

import com.demo.blog.auth.dto.SignUpInitiateRequest
import com.demo.blog.auth.dto.SignUpInitiateResponse
import com.demo.blog.auth.dto.SignUpResponse
import com.demo.blog.auth.dto.VerificationCodeRequest
import com.demo.blog.user.repository.RoleRepository
import com.demo.blog.user.repository.UserRepository
import com.demo.blog.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignUpService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val verificationCodeService: VerificationCodeService,
    private val recaptchaService: RecaptchaService,
    private val jwtService: JwtService,
    private val userService: UserService,
    private val customUserDetailsService: UserDetailsService
) {

    @Transactional
    fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest) : SignUpInitiateResponse {
        val clientIp = getClientIp(httpRequest)

        // 1. Verify reCAPTCHA v2 or v3 token (prioritize v2 if available)
        if (verifyRecaptchaTokens(request, clientIp)) {
            return SignUpInitiateResponse("recaptcha_v2_required", "verify_with_recaptcha_v2")
        }

        // 2. 기본 유효성 검사
        if (userService.existsByEmail(request.email)) {
            throw RuntimeException("Email already exists")
        }
        // TODO: IP 기반 Rate Limiting 검사
//        if (isRateLimited(clientIp)) {
//            throw RuntimeException("Rate limit exceeded")
//        }

        // 3. 인증코드 발송
        if (!verificationCodeService.hasRecentValidCode(request.email)) {
            try {
                verificationCodeService.generateAndSend(request.email, request.name)
            } catch (e: Exception) {
                throw RuntimeException("Failed to send verification code", e)
            }
        }

        return SignUpInitiateResponse("success", "enter_verification_code")
    }

    private fun verifyRecaptchaTokens(request: SignUpInitiateRequest, clientIp: String?): Boolean {
        val isRecaptchaValid: Boolean
        if (!request.recaptchaV2Token.isNullOrBlank()) {
            isRecaptchaValid = recaptchaService.isValidRecaptchaV2(request.recaptchaV2Token, clientIp)
            if (!isRecaptchaValid) {
                throw RuntimeException()
            }
        } else {
            isRecaptchaValid = recaptchaService.isValidRecaptchaV3(request.recaptchaV3Token, clientIp)
            if (!isRecaptchaValid) {
                // Require reCAPTCHA v2 challenge if v3 score is low
                return true
            }
        }
        return false
    }

    @Transactional
    fun verifyCodeAndCompleteSignUp(request: VerificationCodeRequest) : SignUpResponse {
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
}