package com.demo.blog.auth.service

import com.demo.blog.auth.domain.RefreshToken

interface RefreshTokenService {

    fun createOrUpdateOpaqueRefreshToken(userId: String): String

    fun findByTokenAndVerify(token: String): RefreshToken

    fun deleteRefreshTokenByToken(token: String): Int
}