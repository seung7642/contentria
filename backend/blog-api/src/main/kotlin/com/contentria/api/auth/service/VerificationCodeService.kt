package com.contentria.api.auth.service

import com.contentria.api.auth.dto.VerificationCodeCacheDto
import com.contentria.api.auth.service.VerificationCodeCacheService
import com.contentria.api.config.properties.AppProperties
import com.contentria.common.mail.MailService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.ZonedDateTime

private val logger = KotlinLogging.logger {}

@Service
class VerificationCodeService(
    private val verificationCodeCacheService: VerificationCodeCacheService,
    private val mailService: MailService,
    private val appProperties: AppProperties
) {

    private val ttlMinutes: Long = appProperties.auth.verificationCode.ttlMinutes

    fun generateAndSend(email: String, name: String): String {
        val code = generateRandomCode()
        val now = ZonedDateTime.now()

        val codeData = VerificationCodeCacheDto(
            code = code,
            email = email,
            name = name,
            attempts = 0,
            createdAt = now,
            expiresAt = now.plusMinutes(10)
        )

        verificationCodeCacheService.saveVerificationCode(email, codeData, ttlMinutes)

        mailService.sendVerificationEmail(email, name, code)

        return code
    }

    fun verify(email: String, code: String): Boolean {
        val codeData = verificationCodeCacheService.getVerificationCode(email) ?: return false

        if (codeData.attempts >= 3) {
            verificationCodeCacheService.deleteVerificationCode(email)
            return false
        }

        if (codeData.code == code) {
            verificationCodeCacheService.deleteVerificationCode(email)
            return true
        }

        verificationCodeCacheService.incrementVerificationAttempts(email)
        return false
    }

    fun hasRecentValidCode(email: String): Boolean {
        return verificationCodeCacheService.hasRecentVerificationCode(email)
    }

    private fun generateRandomCode(): String {
        return (100000..999999).random().toString()
    }
}