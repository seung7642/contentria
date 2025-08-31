package com.contentria.api.auth.service

import com.contentria.api.config.properties.AppProperties
import com.contentria.api.user.domain.User
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

private val log = KotlinLogging.logger {}

@Service
class JwtService(
    private val appProperties: AppProperties
) {
    private val secretKey: SecretKey by lazy {
        try {
            val keyBytes = Decoders.BASE64.decode(appProperties.auth.jwt.secret)
            Keys.hmacShaKeyFor(keyBytes)
        } catch (e: IllegalArgumentException) {
            log.error(e) { "Invalid JWT secret key from properties: ${e.message}" }
            throw RuntimeException("Invalid JWT secret key configuration", e)
        }
    }

    fun generateAccessToken(user: User): String {
        val expiration = Instant.now().plus(appProperties.auth.jwt.accessTokenExpiration)
        val extraClaims = mapOf(
            "userId" to user.id,
            "roles" to user.getAuthorities().map { it.authority }
        )
        return generateToken(user.email, extraClaims, Date.from(expiration))
    }

    fun generateRefreshToken(user: User): String {
        val expiration = Instant.now().plus(appProperties.auth.jwt.refreshTokenExpiration)
        return generateToken(user.email, emptyMap(), Date.from(expiration))
    }

    private fun generateToken(subject: String, extraClaims: Map<String, Any>, expiration: Date): String {
        return Jwts.builder()
            .claims(extraClaims)
            .subject(subject)
            .issuedAt(Date())
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    fun isTokenValid(token: String, userEmail: String): Boolean {
        try {
            val username = getUsernameFromToken(token)
            return username == userEmail && !isTokenExpired(token)
        } catch (e: JwtException) {
            log.debug { "JWT validation failed: ${e.message}" }
            return false
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return getClaimFromToken(token) { it.expiration }.before(Date())
    }

    private fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}