package com.contentria.api.common.ratelimit.service

import com.contentria.api.common.ratelimit.dto.RateLimitResult
import com.contentria.api.common.ratelimit.service.RateLimitService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class RateLimitServiceImpl(
    private val redisTemplate: StringRedisTemplate
) : RateLimitService {

    override fun checkRateLimit(
        clientIp: String,
        action: String,
        windowSeconds: Long,
        maxRequests: Int
    ): RateLimitResult {
        return RateLimitResult(
            allowed = TODO(),
            currentCount = TODO(),
            remainingRequests = TODO(),
            resetTimeSeconds = TODO(),
            retryAfterSeconds = TODO()
        )
    }

    override fun checkRateLimitOrThrow(
        clientIp: String,
        action: String,
        windowSeconds: Long,
        maxRequests: Int
    ) {
        TODO("Not yet implemented")
    }
}