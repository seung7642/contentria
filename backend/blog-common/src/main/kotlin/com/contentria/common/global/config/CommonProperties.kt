package com.contentria.common.global.config

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "common")
@Validated
data class CommonProperties(
    @field:Valid val mail: MailProperties,
)

@Validated
data class MailProperties(
    @field:Valid val mailgun: MailgunProperties
)

@Validated
data class MailgunProperties(
    @field:NotBlank val domain: String,
    @field:NotBlank val fromAddress: String,
)