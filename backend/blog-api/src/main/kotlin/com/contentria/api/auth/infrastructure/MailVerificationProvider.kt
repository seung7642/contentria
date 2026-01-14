package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.VerificationCodeProvider
import com.contentria.api.auth.application.dto.VerificationCodeCacheDto
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.common.mail.MailService
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

@Component
class MailVerificationProvider(
    private val mailService: MailService,
    private val cacheManager: CacheManager
) : VerificationCodeProvider {

    override fun sendVerificationCode(email: String, name: String?): String {
        val code = generateRandomCode()

        mailService.send(email, name, code)

        val cache = cacheManager.getCache(CACHE_NAME)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_CACHE)
        cache?.put(email, VerificationCodeCacheDto(code, email, name))

        return code
    }

    private fun generateRandomCode(): String = (100000..999999).random().toString()

    override fun verifyCode(email: String, code: String): Boolean {
        val cache = cacheManager.getCache(CACHE_NAME)
        val cachedData = cache?.get(email, VerificationCodeCacheDto::class.java) ?: return false

        if (cachedData.code == code) {
            cache.evict(email)
            return true
        }
        return false
    }

    companion object {
        private const val CACHE_NAME = "verificationCode"
    }
}