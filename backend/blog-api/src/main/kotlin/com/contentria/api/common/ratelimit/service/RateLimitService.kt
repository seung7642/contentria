package com.contentria.api.common.ratelimit.service

import com.contentria.api.common.ratelimit.dto.RateLimitResult

interface RateLimitService {

    fun checkRateLimit(
        clientIp: String,
        action: String = "default",
        windowSeconds: Long = 3600, // 1 hour
        maxRequests: Int = 5 // Default to 5 requests per hour
    ): RateLimitResult

    fun checkRateLimitOrThrow(
        clientIp: String,
        action: String = "default",
        windowSeconds: Long = 3600,
        maxRequests: Int = 5
    )
}