package com.demo.blog.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan
class AppConfig

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.google")
data class GoogleProperties(
    val clientId: String = "",
    val clientSecret: String = "",
    val redirectUri: String = ""
)