package com.contentria.api.config.properties

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
@Validated
data class GoogleProperties(
    @field:NotBlank val clientId: String,
    @field:NotBlank val clientSecret: String,
    @field:NotBlank val redirectUri: String
)