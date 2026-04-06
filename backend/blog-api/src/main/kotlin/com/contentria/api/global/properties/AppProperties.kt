package com.contentria.api.global.properties

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import java.time.Duration

@ConfigurationProperties(prefix = "app")
@Validated
data class AppProperties(
    @field:Valid val auth: AuthProperties,
    @field:Valid val cors: CorsProperties,
    @field:Valid val r2: R2Properties,
)

@Validated
data class AuthProperties(
    @field:Valid val jwt: JwtProperties,
    @field:Valid val cookie: CookieProperties,
    @field:Valid val oidc: OidcProperties,
    @field:Valid val verificationCode: VerificationCodeProperties,
    @field:Valid val recaptcha: RecaptchaProperties
)

@Validated
data class JwtProperties(
    @field:NotBlank val secret: String,
    val accessTokenExpiration: Duration,
    val refreshTokenExpiration: Duration
)

@Validated
data class CookieProperties(
    val accessTokenName: String,
    val refreshTokenName: String,
    val accessTokenPath: String,
    val refreshTokenPath: String,
    val secure: Boolean = true
)

@Validated
data class OidcProperties(
    val successRedirectUrl: String,
)

@Validated
data class VerificationCodeProperties(
    val ttlMinutes: Long = 10L,
    val length: Int = 6,
    val redisKeyPrefix: String = "verification_code:email:",
)

@Validated
data class CorsProperties(
    val allowedOrigins: List<String>
)

@Validated
data class R2Properties(
    @field:NotBlank val accessKeyId: String,
    @field:NotBlank val secretAccessKey: String,
    @field:NotBlank val endpoint: String,
    @field:NotBlank val bucketName: String,
    @field:NotBlank val publicUrl: String,
    val presignedUrlTtlMinutes: Long = 10L,
    val maxFileSizeBytes: Long = 10 * 1024 * 1024, // 10MB
)

@Validated
data class RecaptchaProperties(
    @field:NotBlank val v2SecretKey: String,
    @field:NotBlank val v3SecretKey: String,
    @field:DecimalMin(value = "0.0") @field:DecimalMax(value = "1.0")
    val scoreThreshold: Double = 0.5,
    val siteVerifyUrl: String = "https://www.google.com/recaptcha/api/siteverify",
    val expectedHostname: String? = null,
    val expectedActions: Map<String, String> = emptyMap()
)
