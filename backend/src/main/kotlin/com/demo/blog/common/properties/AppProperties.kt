package com.demo.blog.common.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val auth: AuthProperties,
    val cors: CorsProperties
)

data class AuthProperties(
    val jwt: JwtProperties,
    val cookie: CookieProperties,
    val oidc: OidcProperties,
    val verificationCode: VerificationCodeProperties
)

data class JwtProperties(
    val secret: String,
    val accessTokenExpirationMs: Long,
    val refreshTokenExpirationMs: Long
)

data class CookieProperties(
    val accessTokenName: String,
    val refreshTokenName: String,
    val accessTokenPath: String,
    val refreshTokenPath: String
)

data class OidcProperties(
    val successRedirectUrl: String,
)

data class VerificationCodeProperties(
    val ttlMinutes: Long = 10L,
    val length: Int = 6,
    val redisKeyPrefix: String = "verification_code:email:",
)

data class CorsProperties(
    val allowedOrigins: List<String>
)