package com.contentria.api.auth.cache.service

import com.contentria.api.auth.cache.dto.VerificationCodeCache

interface AuthCacheService {

    fun saveVerificationCode(email: String, codeData: VerificationCodeCache, ttlMinutes: Long = 10)

    fun getVerificationCode(email: String): VerificationCodeCache?

    fun deleteVerificationCode(email: String): Boolean

    fun incrementVerificationAttempts(email: String): Int?

    fun hasRecentVerificationCode(email: String): Boolean
}