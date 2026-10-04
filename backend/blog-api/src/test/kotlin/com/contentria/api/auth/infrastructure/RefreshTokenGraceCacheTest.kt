package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.dto.RotatedTokenInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class RefreshTokenGraceCacheTest {

    private lateinit var cache: RefreshTokenGraceCache

    @BeforeEach
    fun setUp() {
        cache = RefreshTokenGraceCache()
    }

    @Test
    fun `returns null when oldToken is not present`() {
        assertThat(cache.get("missing")).isNull()
    }

    @Test
    fun `returns the rotated info that was put under the same oldToken`() {
        val oldToken = "A"
        val rotated = RotatedTokenInfo(newToken = "B", userId = UUID.randomUUID())

        cache.put(oldToken, rotated)

        assertThat(cache.get(oldToken)).isEqualTo(rotated)
    }

    @Test
    fun `overwrites existing entry on subsequent put with same oldToken`() {
        val oldToken = "A"
        val first = RotatedTokenInfo(newToken = "B", userId = UUID.randomUUID())
        val second = RotatedTokenInfo(newToken = "C", userId = UUID.randomUUID())

        cache.put(oldToken, first)
        cache.put(oldToken, second)

        assertThat(cache.get(oldToken)).isEqualTo(second)
    }

    @Test
    fun `entries for different oldTokens do not collide`() {
        val rotatedA = RotatedTokenInfo(newToken = "Anew", userId = UUID.randomUUID())
        val rotatedX = RotatedTokenInfo(newToken = "Xnew", userId = UUID.randomUUID())

        cache.put("A", rotatedA)
        cache.put("X", rotatedX)

        assertThat(cache.get("A")).isEqualTo(rotatedA)
        assertThat(cache.get("X")).isEqualTo(rotatedX)
    }
}
