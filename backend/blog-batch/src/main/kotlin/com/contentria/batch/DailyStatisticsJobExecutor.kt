package com.contentria.batch

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class DailyStatisticsJobExecutor(
    private val jobLauncher: JobLauncher,
    private val dailyStatisticsJob: Job
) : BatchJobExecutor {

    override fun getJobName(): String = "dailyStatisticsJob"

    override fun execute() {
        log.info { "Starting daily statistics job" }

        val targetDate = LocalDate.now().minusDays(1).toString()

        val jobParameters = JobParametersBuilder()
            .addString("targetDate", targetDate)
            .addString("runId", LocalDateTime.now().toString())
            .toJobParameters()

        jobLauncher.run(dailyStatisticsJob, jobParameters)
    }
}