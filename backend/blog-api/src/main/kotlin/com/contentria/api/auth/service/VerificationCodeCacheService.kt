package com.contentria.api.auth.service

import com.contentria.api.auth.dto.VerificationCodeCacheDto
import com.contentria.common.cache.CacheService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class VerificationCodeCacheService(
    private val cacheService: CacheService,
    private val objectMapper: ObjectMapper
) {

    fun saveVerificationCode(
        email: String,
        codeData: VerificationCodeCacheDto,
        ttlMinutes: Long
    ) {
        val key = VERIFICATION_CODE_PREFIX + email
        val jsonValue = objectMapper.writeValueAsString(codeData)
        cacheService.set(key, jsonValue, ttlMinutes * 60)
    }

    fun getVerificationCode(email: String): VerificationCodeCacheDto? {
        val key = VERIFICATION_CODE_PREFIX + email
        val jsonValue = cacheService.get(key) ?: return null

        return try {
            objectMapper.readValue(jsonValue, VerificationCodeCacheDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun deleteVerificationCode(email: String): Boolean {
        val key = VERIFICATION_CODE_PREFIX + email
        return cacheService.delete(key)
    }

    fun incrementVerificationAttempts(email: String): Int? {
        val codeData = getVerificationCode(email) ?: return null
        val updatedData = codeData.copy(attempts = codeData.attempts + 1)

        val remainingTtl = cacheService.getTtl(VERIFICATION_CODE_PREFIX + email)
        if (remainingTtl > 0) {
            saveVerificationCode(email, updatedData, remainingTtl / 60)
        }

        return updatedData.attempts
    }

    fun hasRecentVerificationCode(email: String): Boolean {
        return cacheService.exists(VERIFICATION_CODE_PREFIX + email)
    }

    companion object {
        private const val VERIFICATION_CODE_PREFIX = "verification_code:"
    }
}