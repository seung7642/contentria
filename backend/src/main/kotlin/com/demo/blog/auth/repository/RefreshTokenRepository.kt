package com.demo.blog.auth.repository

import com.demo.blog.auth.domain.RefreshToken
import com.demo.blog.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {

    fun findByToken(token: String): RefreshToken?

    fun findByUser(user: User): RefreshToken?

    fun deleteByToken(token: String): Int
}