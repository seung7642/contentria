package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.VerificationCodeProvider
import com.contentria.api.auth.application.dto.VerificationCodeCacheDto
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.common.infrastructure.email.EmailService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component
import java.security.SecureRandom

private val log = KotlinLogging.logger {  }

@Component
class MailVerificationProvider(
    private val emailService: EmailService,
    private val cacheManager: CacheManager
) : VerificationCodeProvider {

    private val secureRandom = SecureRandom()

    override fun sendVerificationCode(email: String, name: String?) {
        val cache = getCacheOrThrow()

        if (cache.get(email) != null) {
            // Reuse the pending code so the email already in the inbox stays valid.
            log.info { "Verification code already pending; skipping send." }
            return
        }

        val code = generateRandomCode()

        emailService.sendAuthCodeEmail(email, code)

        cache.put(email, VerificationCodeCacheDto(code, email, name))

        log.debug { "Verification code sent successfully" }
    }

    private fun generateRandomCode(): String {
        return (secureRandom.nextInt(900000) + 100000).toString()
    }

    override fun verifyCode(email: String, code: String) {
        val cache = getCacheOrThrow()

        val cachedData = cache.get(email, VerificationCodeCacheDto::class.java)

        if (cachedData == null) {
            log.warn { "Verification failed: code expired or not found" }
            throw ContentriaException(ErrorCode.INVALID_VERIFICATION_CODE)
        }

        if (cachedData.code != code) {
            log.warn { "Verification failed: invalid code" }
            throw ContentriaException(ErrorCode.INVALID_VERIFICATION_CODE)
        }

        cache.evict(email)
        log.debug { "Verification successful" }
    }

    private fun getCacheOrThrow(): Cache {
        return cacheManager.getCache(CACHE_NAME)
            ?: run {
                log.error { "Cache configuration not found for name: $CACHE_NAME" }
                throw ContentriaException(ErrorCode.INTERNAL_SERVER_ERROR)
            }
    }

    companion object {
        private const val CACHE_NAME = "verificationCode"
    }
}