package com.contentria.api.config

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationPropertiesScan
class AppConfig