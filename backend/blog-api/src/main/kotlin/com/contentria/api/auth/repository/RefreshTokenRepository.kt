package com.contentria.api.auth.repository

import com.contentria.api.auth.domain.RefreshToken
import com.contentria.api.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByToken(token: String): RefreshToken?

    fun findByUser(user: User): RefreshToken?

    fun deleteByToken(token: String): Int
}