package com.contentria.api.global.properties

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated
import java.time.Duration

@ConfigurationProperties(prefix = "app")
@Validated
data class AppProperties(
    @NestedConfigurationProperty @field:Valid val auth: AuthProperties,
    @NestedConfigurationProperty @field:Valid val cors: CorsProperties,
    @NestedConfigurationProperty @field:Valid val r2: R2Properties,
    @NestedConfigurationProperty @field:Valid val video: VideoProperties = VideoProperties(),
)

@Validated
data class AuthProperties(
    @NestedConfigurationProperty @field:Valid val jwt: JwtProperties,
    @NestedConfigurationProperty @field:Valid val cookie: CookieProperties,
    @NestedConfigurationProperty @field:Valid val oidc: OidcProperties,
    @NestedConfigurationProperty @field:Valid val verificationCode: VerificationCodeProperties,
    @NestedConfigurationProperty @field:Valid val recaptcha: RecaptchaProperties,
    @NestedConfigurationProperty @field:Valid val refreshToken: RefreshTokenProperties = RefreshTokenProperties()
)

@Validated
data class RefreshTokenProperties(
    @field:Min(1) val maxPerUser: Int = 5
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
data class VideoProperties(
    val maxFileSizeBytes: Long = 500L * 1024 * 1024, // 500MB (single PUT cap; multipart deferred)
    val allowedContentTypes: Set<String> = setOf(
        "video/mp4",
        "video/quicktime",   // .mov
        "video/webm",
        "video/x-matroska",  // .mkv
    ),
)

/** Media-upload usage policy. R2 connection info itself lives in blog-common's R2Properties (common.r2.*). */
@Validated
data class R2Properties(
    @field:NotBlank val publicUrl: String,
    val presignedUrlTtlMinutes: Long = 10L,
    val maxFileSizeBytes: Long = 10 * 1024 * 1024, // 10MB
    val dailyUploadLimitBytes: Long = 100 * 1024 * 1024, // 100MB per user per day
    val maxImagesPerPost: Int = 20,
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
