package com.contentria.api.auth.service

import com.contentria.api.auth.dto.VerificationCodeCacheDto
import com.contentria.common.mail.MailService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class VerificationCodeService(
    private val mailService: MailService,
) {
    @Autowired
    private lateinit var cacheManager: CacheManager

    @Cacheable(cacheNames = [CACHE_NAME], key = "#email", unless = "#result == null")
    fun send(email: String, name: String? = null): VerificationCodeCacheDto {
        val code = generateRandomCode()
        mailService.send(email, name, code)

        return VerificationCodeCacheDto(code = code, email = email, name = name)
    }

    @CacheEvict(cacheNames = [CACHE_NAME], key = "#email", condition = "#result == true")
    fun verify(email: String, code: String): Boolean {
        val cachedData = getCachedCode(email) ?: return false
        return if (cachedData.code == code) true else false
    }

    private fun getCachedCode(email: String): VerificationCodeCacheDto? {
        val cache = cacheManager.getCache(CACHE_NAME)
        return cache?.get(email, VerificationCodeCacheDto::class.java)
    }

    private fun generateRandomCode(): String = (100000..999999).random().toString()

    companion object {
        private const val CACHE_NAME = "verificationCode"
    }
}