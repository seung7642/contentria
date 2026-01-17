package com.contentria.api.auth.application

import com.contentria.api.auth.application.dto.*
import com.contentria.api.user.application.UserService
import com.contentria.api.user.application.dto.UserInfo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class AuthFacade(
    private val credentialService: CredentialService,
    private val refreshTokenService: RefreshTokenService,
    private val verificationCodeProvider: VerificationCodeProvider,
    private val captchaProvider: CaptchaProvider,
    private val userService: UserService,
    private val tokenProvider: TokenProvider
) {
    @Transactional
    fun initiate(command: SignUpInitiateCommand) {
        captchaProvider.verify(command.captcha)

        val user = userService.createUnverifiedUser(command.email, command.name)

        credentialService.createPasswordCredential(
            userId = user.userId,
            email = command.email,
            rawPassword = command.password
        )

        verificationCodeProvider.sendVerificationCode(command.email, command.name)
    }

    @Transactional
    fun verifyCode(command: VerifyCodeCommand): VerifyCodeInfo {
        verificationCodeProvider.verifyCode(command.email, command.verificationCode)

        val user = userService.activateUserByEmail(command.email)
        val (accessToken, refreshToken) = generateTokens(user)

        return VerifyCodeInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user
        )
    }

    @Transactional
    fun login(command: LoginCommand): LoginInfo {
        captchaProvider.verify(command.captcha)

        val credential = credentialService.authenticate(command.email, command.password)

        val user = userService.getActiveUserInfo(credential.userId)
        val (accessToken, refreshToken) = generateTokens(user)

        log.info { "User logged in successfully: ${user.email}" }

        return LoginInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user
        )
    }

    @Transactional
    fun loginWithSocial(command: SocialLoginCommand): LoginInfo {
        val user = userService.upsertSocialUser(
            email = command.email,
            name = command.name,
            pictureUrl = command.picture
        )

        credentialService.upsertSocialCredential(
            userId = user.userId,
            email = user.email,
            provider = command.provider,
            providerId = command.providerId
        )
        val (accessToken, refreshToken) = generateTokens(user)

        return LoginInfo(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user
        )
    }

    @Transactional
    fun sendOtp(command: SendOtpCommand) {
        captchaProvider.verify(command.captcha)

        val user = userService.getActiveUserInfo(command.email)

        verificationCodeProvider.sendVerificationCode(command.email, user.name)
    }

    @Transactional
    fun refreshTokens(oldRefreshTokenValue: String): RefreshedTokensInfo {
        val validRefreshToken = refreshTokenService.findValidToken(oldRefreshTokenValue)
        val user = userService.getActiveUserInfo(validRefreshToken.userId)

        val (accessToken, refreshToken) = generateTokens(user)

        return RefreshedTokensInfo(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun generateTokens(userInfo: UserInfo): Pair<String, String> {

        val authTokenCommand = AuthTokenCommand(
            userId = userInfo.userId,
            email = userInfo.email,
            roles = userInfo.roles
        )

        val accessToken = tokenProvider.generateAccessToken(authTokenCommand)
        val refreshToken = refreshTokenService.upsertRefreshToken(userInfo.userId)
        return Pair(accessToken, refreshToken)
    }
}