package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.domain.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshTokenJpaRepository : JpaRepository<RefreshToken, UUID> {

    fun findByToken(token: String): RefreshToken?

    fun findByUserId(userId: UUID): RefreshToken?

    fun deleteByToken(token: String): Int
}