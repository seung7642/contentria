package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.domain.RefreshToken
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface RefreshTokenJpaRepository : JpaRepository<RefreshToken, UUID> {

    fun findByToken(token: String): RefreshToken?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RefreshToken r WHERE r.token = :token")
    fun findByTokenForUpdate(@Param("token") token: String): RefreshToken?

    fun deleteByToken(token: String): Int

    /**
     * Deletes refresh tokens for [userId] beyond the [keep] most recent rows (by created_at).
     * Native query because JPQL has unreliable OFFSET support in subqueries.
     */
    @Modifying
    @Query(
        value = """
            DELETE FROM refresh_tokens
            WHERE id IN (
                SELECT id FROM refresh_tokens
                WHERE user_id = :userId
                ORDER BY created_at DESC
                OFFSET :keep
            )
        """,
        nativeQuery = true
    )
    fun pruneOldest(@Param("userId") userId: UUID, @Param("keep") keep: Int): Int
}
