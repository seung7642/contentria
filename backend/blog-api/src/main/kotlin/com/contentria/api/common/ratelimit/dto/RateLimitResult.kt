package com.contentria.api.common.ratelimit.dto

data class RateLimitResult(
    val allowed: Boolean,
    val currentCount: Int,
    val remainingRequests: Int,
    val resetTimeSeconds: Long? = null,
    val retryAfterSeconds: Long? = null
)
