package com.contentria.api.auth.application

import com.contentria.api.auth.application.dto.*
import com.contentria.api.auth.controller.dto.SignUpInitiateResponse
import com.contentria.api.auth.controller.dto.VerifyCodeRequest
import com.contentria.api.auth.domain.CredentialRepository
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.global.util.IpResolver
import com.contentria.api.user.application.UserService
import com.contentria.api.user.controller.dto.CurrentUserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class AuthFacade(
    private val credentialService: CredentialService,
    private val refreshTokenService: RefreshTokenService,
    private val verificationCodeService: VerificationCodeService,
    private val captchaVerifier: CaptchaVerifier,
    private val userService: UserService,
    private val jwtService: JwtService,
    private val credentialRepository: CredentialRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun initiate(command: SignUpInitiateCommand): SignUpInitiateResponse {
        if (!captchaVerifier.verify(command.captcha)) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        val user = userService.createUnverifiedUser(command.email, command.name, command.password)
        credentialService.createPasswordCredential(
            userId = user.id!!,
            email = command.email,
            rawPassword = command.password
        )

        verificationCodeService.send(command.email, command.name)

        return SignUpInitiateResponse("success")
    }

    @Transactional
    fun verifyCode(request: VerifyCodeRequest): VerifyCodeInfo {
        val result = verificationCodeService.verify(request.email, request.verificationCode)
        if (!result) {
            throw ContentriaException(ErrorCode.INVALID_VERIFICATION_CODE)
        }

        val user = userService.activateUserByEmail(request.email)
        val authTokenInfo = AuthTokenInfo(
            userId = user.id!!,
            email = user.email,
            roles = user.userRoles.map { it.role.name }
        )

        val accessToken = jwtService.generateAccessToken(authTokenInfo)
        val refreshToken = jwtService.generateRefreshToken(authTokenInfo)

        return VerifyCodeInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = CurrentUserResponse.from(user)
        )
    }

    @Transactional
    fun login(command: LoginCommand): LoginInfo {
        if (!captchaVerifier.verify(command.captcha)) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        val credential = credentialRepository.findByEmail(command.email)
            ?: throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(command.password, credential.password)) {
            throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)
        }

        val user = userService.findActiveUserById(credential.userId)

        val authTokenInfo = AuthTokenInfo(
            userId = user.id!!,
            email = user.email,
            roles = user.userRoles.map { it.role.name }
        )

        val accessToken = jwtService.generateAccessToken(authTokenInfo)
        val refreshToken = refreshTokenService.upsertRefreshToken(user.id!!)

        log.info { "User logged in successfully: ${user.email}" }

        return LoginInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = CurrentUserResponse.from(user)
        )
    }

    @Transactional
    fun sendOtp(command: SendOtpCommand) {
        val user = userService.findByEmail(command.email)
            ?: throw ContentriaException(ErrorCode.INVALID_CREDENTIALS)

        if (!captchaVerifier.verify(command.captcha)) {
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        if (!user.status.isActive()) {
            throw ContentriaException(ErrorCode.USER_NOT_ACTIVATED)
        }

        verificationCodeService.send(command.email)
    }

    @Transactional
    fun refreshTokens(oldRefreshTokenValue: String): RefreshedTokensDto {
        val refreshToken = refreshTokenService.findValidToken(oldRefreshTokenValue)

        val user = userService.findActiveUserById(refreshToken.userId)
        val authTokenInfo = AuthTokenInfo(
            userId = user.id!!,
            email = user.email,
            roles = user.userRoles.map { it.role.name }
        )

        val newAccessToken = jwtService.generateAccessToken(authTokenInfo)
        val newRefreshToken = refreshTokenService.upsertRefreshToken(refreshToken.userId)

        return RefreshedTokensDto(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}