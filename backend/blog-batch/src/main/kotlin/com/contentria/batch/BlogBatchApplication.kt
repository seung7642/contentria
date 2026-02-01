package com.contentria.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlogBatchApplication

fun main(args: Array<String>) {
    runApplication<BlogBatchApplication>(*args)
}
