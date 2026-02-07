package com.contentria.api.global.config

import com.contentria.api.global.properties.AppProperties
import com.contentria.api.global.properties.GoogleProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode

@EnableConfigurationProperties(GoogleProperties::class, AppProperties::class)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@ConfigurationPropertiesScan
@Configuration
class AppConfig