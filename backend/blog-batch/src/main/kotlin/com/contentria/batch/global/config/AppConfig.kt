package com.contentria.batch.global.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(AppProperties::class)
@Configuration
class AppConfig