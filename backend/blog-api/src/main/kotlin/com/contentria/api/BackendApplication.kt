package com.contentria.api

import com.contentria.api.common.config.GoogleProperties
import com.contentria.api.common.properties.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableConfigurationProperties(GoogleProperties::class, AppProperties::class)
@ComponentScan(basePackages = ["com.contentria.common", "com.contentria.api"])
class BackendApplication

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
