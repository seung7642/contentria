package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.TokenProvider
import com.contentria.api.auth.application.dto.AuthTokenCommand
import com.contentria.api.global.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

private val log = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    private val appProperties: AppProperties
) : TokenProvider {

    private val secretKey: SecretKey by lazy {
        try {
            val keyBytes = Decoders.BASE64.decode(appProperties.auth.jwt.secret)
            Keys.hmacShaKeyFor(keyBytes)
        } catch (e: IllegalArgumentException) {
            log.error(e) { "Invalid JWT secret key from properties: ${e.message}" }
            throw RuntimeException("Invalid JWT secret key configuration", e)
        }
    }

    override fun generateAccessToken(authToken: AuthTokenCommand): String {
        val expiration = Instant.now().plus(appProperties.auth.jwt.accessTokenExpiration)
        val extraClaims = mapOf(
            "userId" to authToken.userId,
            "roles" to authToken.roles
        )
        return generateToken(authToken.email, extraClaims, Date.from(expiration))
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

    override fun validateToken(token: String): Boolean {
        return try {
            val claims = getAllClaimsFromToken(token)
            !claims.expiration.before(Date())
        } catch (e: JwtException) {
            log.debug { "JWT validation failed: ${e.message}" }
            false
        } catch (e: Exception) {
            log.debug { "JWT string is empty or null" }
            false
        }
    }

    override fun extractSubject(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}