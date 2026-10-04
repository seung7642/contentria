package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.dto.RotatedTokenInfo
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RefreshTokenGraceCache {

    private val cache: Cache<String, RotatedTokenInfo> =
        Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(GRACE_TTL_SECONDS))
            .maximumSize(MAX_ENTRIES)
            .build()

    fun put(oldToken: String, rotated: RotatedTokenInfo) {
        cache.put(oldToken, rotated)
    }

    fun get(oldToken: String): RotatedTokenInfo? {
        return cache.getIfPresent(oldToken)
    }

    companion object {
        const val GRACE_TTL_SECONDS = 10L
        const val MAX_ENTRIES = 10_000L
    }
}
