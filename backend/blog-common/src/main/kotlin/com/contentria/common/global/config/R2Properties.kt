package com.contentria.common.global.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/** Cloudflare R2 (S3-compatible) connection info, shared by blog-api/blog-batch/blog-worker. */
@ConfigurationProperties(prefix = "common.r2")
@Validated
data class R2Properties(
    @field:NotBlank val accessKeyId: String,
    @field:NotBlank val secretAccessKey: String,
    @field:NotBlank val endpoint: String,
    @field:NotBlank val bucketName: String,
)
