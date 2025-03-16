package com.demo.blog.common.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

    @Value("\${app.auth.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${app.auth.jwt.expiration-ms}")
    private var jwtExpirationMs: Long = 0

    private val secretKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userDetails: UserDetails): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpirationMs)

        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey) // 알고리즘 자동 선택 (HS256)
            .compact()
    }

    fun getUsernameFromJWT(token: String): String {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        return claims.subject
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (ex: JwtException) {
            return false
        }
    }
}