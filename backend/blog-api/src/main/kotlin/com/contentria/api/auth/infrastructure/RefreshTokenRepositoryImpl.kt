package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.domain.RefreshToken
import com.contentria.api.auth.domain.RefreshTokenRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RefreshTokenRepositoryImpl(
    private val jpaRepository: RefreshTokenJpaRepository
) : RefreshTokenRepository {

    override fun findById(id: UUID): RefreshToken? {
        return jpaRepository.findById(id).orElse(null)
    }

    override fun save(refreshToken: RefreshToken): RefreshToken {
        return jpaRepository.save(refreshToken)
    }

    override fun delete(refreshToken: RefreshToken) {
        jpaRepository.delete(refreshToken)
    }

    override fun deleteAll(refreshTokens: List<RefreshToken>) {
        jpaRepository.deleteAll(refreshTokens)
    }

    override fun findByToken(token: String): RefreshToken? {
        return jpaRepository.findByToken(token)
    }

    override fun findByUserId(userId: UUID): RefreshToken? {
        return jpaRepository.findByUserId(userId)
    }

    override fun deleteByToken(token: String): Int {
        return jpaRepository.deleteByToken(token)
    }
}