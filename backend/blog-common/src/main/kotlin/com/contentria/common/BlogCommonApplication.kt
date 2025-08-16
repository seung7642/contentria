package com.contentria.common

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BlogCommonApplication

fun main(args: Array<String>) {
    runApplication<BlogCommonApplication>(*args)
}
