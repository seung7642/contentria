package com.contentria.common.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(CommonProperties::class)
@Configuration
class CommonConfig {
}