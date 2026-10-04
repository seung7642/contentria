package com.contentria.common.global.config

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "common.mail")
@Validated
data class MailProperties(
    @NestedConfigurationProperty @field:Valid val mailgun: MailgunProperties
)

@Validated
data class MailgunProperties(
    @field:NotBlank val fromAddress: String,
)
