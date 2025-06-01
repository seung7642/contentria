package com.demo.blog.common.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationPropertiesScan
class AppConfig

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
@Validated
data class GoogleProperties(
    @field:NotBlank val clientId: String,
    @field:NotBlank val clientSecret: String,
    @field:NotBlank val redirectUri: String
)