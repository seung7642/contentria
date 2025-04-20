package com.demo.blog.auth.service

import com.demo.blog.common.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

private val logger = KotlinLogging.logger {}

@Service
class JwtService(
    private val appProperties: AppProperties
) {
    private val secretKey: SecretKey by lazy {
        try {
            val keyBytes = Decoders.BASE64.decode(appProperties.auth.jwt.secret)
            Keys.hmacShaKeyFor(keyBytes)
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Invalid JWT secret key from properties: ${e.message}" }
            throw RuntimeException("Invalid JWT secret key configuration", e)
        }
    }

    fun generateAccessToken(userDetails: UserDetails): String {
        return generateToken(userDetails.username, appProperties.auth.jwt.accessTokenExpirationMs)
    }

    private fun generateToken(subject: String, expirationMs: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMs)

        return Jwts.builder()
            .subject(subject)
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
            logger.debug(ex) { "JWT validation failed: ${ex.message}" }
            return false
        }
    }
}