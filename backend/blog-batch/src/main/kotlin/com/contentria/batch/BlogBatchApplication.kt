package com.contentria.batch

import com.contentria.common.global.config.CommonConfig
import com.contentria.common.global.config.jpa.JpaConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import

@ComponentScan(basePackages = ["com.contentria"])
@Import(
    CommonConfig::class,
    JpaConfig::class
)
@SpringBootApplication
class BlogBatchApplication

fun main(args: Array<String>) {
    runApplication<BlogBatchApplication>(*args)
}
