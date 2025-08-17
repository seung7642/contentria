package com.contentria.common.config

import com.contentria.common.properties.CommonProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CommonProperties::class)
class PropertiesConfig {
}