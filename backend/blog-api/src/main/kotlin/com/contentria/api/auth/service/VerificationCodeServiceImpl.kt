package com.contentria.api.auth.service

import com.contentria.api.auth.cache.dto.VerificationCodeCache
import com.contentria.api.auth.cache.service.AuthCacheService
import com.contentria.api.common.mail.MailService
import com.contentria.api.common.properties.AppProperties
import com.demo.com.contentria.api.auth.service.VerificationCodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Service
class VerificationCodeServiceImpl(
    private val authCacheService: AuthCacheService,
    private val mailService: MailService,
    private val appProperties: AppProperties
) : VerificationCodeService {

    private val ttlMinutes: Long = appProperties.auth.verificationCode.ttlMinutes

    override fun generateAndSend(email: String, name: String): String {
        val code = generateRandomCode()
        val now = ZonedDateTime.now()

        val codeData = VerificationCodeCache(
            code = code,
            email = email,
            name = name,
            attempts = 0,
            createdAt = now,
            expiresAt = now.plusMinutes(10)
        )

        authCacheService.saveVerificationCode(email, codeData, ttlMinutes)

        mailService.sendVerificationEmail(email, name, code)

        return code
    }

    override fun verify(email: String, code: String): Boolean {
        val codeData = authCacheService.getVerificationCode(email) ?: return false

        if (codeData.attempts >= 3) {
            authCacheService.deleteVerificationCode(email)
            return false
        }

        if (codeData.code == code) {
            authCacheService.deleteVerificationCode(email)
            return true
        }

        authCacheService.incrementVerificationAttempts(email)
        return false
    }

    override fun hasRecentValidCode(email: String): Boolean {
        return authCacheService.hasRecentVerificationCode(email)
    }

    private fun generateRandomCode(): String {
        return (100000..999999).random().toString()
    }
}