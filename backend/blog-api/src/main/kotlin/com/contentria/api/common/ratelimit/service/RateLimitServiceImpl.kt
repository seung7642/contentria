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

    private val luaScript = """
        local key = KEYS[1]
        local window = tonumber(ARGV[1])
        local limit = tonumber(ARGV[2])
        local current_time = tonumber(ARGV[3])

        -- Get the current count of requests
        local current = redis.call('GET', key)

        if current == false then
            redis.call('SET', key, 1)
            redis.call('EXPIRE', key, window)
            return {1, limit - 1, window}
        end

        current = tonumber(current)

        if current < limit then
            local new_val = redis.call('INCR', key)
            local ttl = redis.call('TTL', key)
            if ttl == -1 then
                redis.call('EXPIRE', key, window)
                ttl = window
            end
            return {new_val, limit - new_val, ttl}
        else
            local ttl = redis.call('TTL', key)
            return {current, 0, ttl}
        end
    """.trimIndent()

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