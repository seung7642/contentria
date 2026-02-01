package com.contentria.api

import com.contentria.api.global.properties.AppProperties
import com.contentria.api.global.properties.GoogleProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableConfigurationProperties(GoogleProperties::class, AppProperties::class)
@ComponentScan(basePackages = ["com.contentria"])
@EntityScan(basePackages = ["com.contentria"])
@EnableJpaRepositories(basePackages = ["com.contentria"])
class BlogApiApplication

fun main(args: Array<String>) {
    runApplication<BlogApiApplication>(*args)
}
