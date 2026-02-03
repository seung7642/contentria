package com.contentria.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BlogBatchApplication

fun main(args: Array<String>) {
    runApplication<BlogBatchApplication>(*args)
}
