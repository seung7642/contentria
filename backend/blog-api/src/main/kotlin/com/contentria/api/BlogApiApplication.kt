package com.contentria.api

import com.contentria.api.config.properties.AppProperties
import com.contentria.api.config.properties.GoogleProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableConfigurationProperties(GoogleProperties::class, AppProperties::class)
@EnableJpaAuditing
@ComponentScan(basePackages = ["com.contentria.common", "com.contentria.api"])
class BlogApiApplication

fun main(args: Array<String>) {
    runApplication<BlogApiApplication>(*args)
}
