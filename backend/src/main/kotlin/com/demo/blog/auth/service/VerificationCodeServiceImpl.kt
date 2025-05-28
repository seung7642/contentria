package com.demo.blog.auth.service

import com.demo.blog.common.mail.MailService
import com.demo.blog.common.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Service
class VerificationCodeServiceImpl(
    private val stringRedisTemplate: StringRedisTemplate,
    private val mailService: MailService,
    private val appProperties: AppProperties
) : VerificationCodeService {

    private val secureRandom = SecureRandom()
    private val ttlMinutes: Long = appProperties.auth.verificationCode.ttlMinutes
    private val length: Int = appProperties.auth.verificationCode.length
    private val redisKeyPrefix: String = appProperties.auth.verificationCode.redisKeyPrefix

    override fun generateAndSend(email: String, userName: String): String {
        val code = generateRandomCode(length)
        val key = redisKeyPrefix + email

        // 1. Store the code in Redis with TTL
        stringRedisTemplate.opsForValue().set(key, code, ttlMinutes, TimeUnit.MINUTES)

        // 2. Send the verification code via email
        mailService.sendVerificationEmail(email, userName, code)

        return code
    }

    override fun verify(email: String, code: String): Boolean {
        val key = redisKeyPrefix + email
        val storedCode = stringRedisTemplate.opsForValue().get(key)

        if (storedCode != null && storedCode == code) {
            stringRedisTemplate.delete(key)
            return true
        }
        return false
    }

    override fun hasRecentValidCode(email: String): Boolean {
        val key = redisKeyPrefix + email
        return stringRedisTemplate.hasKey(key)
    }

    private fun generateRandomCode(length: Int): String {
        val codeChars = "0123456789"
        return (1..length)
            .map { secureRandom.nextInt(codeChars.length) }
            .map(codeChars::get)
            .joinToString("")
    }
}