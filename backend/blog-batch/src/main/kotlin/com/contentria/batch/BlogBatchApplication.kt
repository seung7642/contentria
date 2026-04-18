package com.contentria.batch

import com.contentria.batch.global.config.AppProperties
import com.contentria.common.global.config.CommonConfig
import com.contentria.common.global.config.jpa.JpaConfig
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@ComponentScan(basePackages = ["com.contentria"])
@Import(
    CommonConfig::class,
    JpaConfig::class
)
@SpringBootApplication
class BlogBatchApplication

fun main(args: Array<String>) {
    val context = runApplication<BlogBatchApplication>(*args) {
        setWebApplicationType(WebApplicationType.NONE)
    }
    val exitCode = SpringApplication.exit(context)
    exitProcess(exitCode)
}

@Component
class BatchRouteRunner(
    private val executors: List<BatchJobExecutor>,
    private val appProperties: AppProperties
) : CommandLineRunner {

    override fun run(vararg args: String) {
        val jobName = appProperties.batch.jobName

        val executor = executors.find { it.getJobName() == jobName }
            ?: throw IllegalArgumentException("No BatchJobExecutor found for jobName: $jobName")

        executor.execute()
    }
}