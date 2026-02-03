package com.contentria.batch.global.scheduler

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Component
class BatchScheduler(
    private val jobLauncher: JobLauncher,
    private val dailyStatisticsJob: Job
) {
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 1시에 실행
    fun runDailyStatisticsJob() {
        val yesterday = LocalDate.now().minusDays(1)
        val yesterdayStr = yesterday.toString()

        log.info { "Daily Statistics Job Started. Target Data: $yesterdayStr"  }

        try {
            val jobParameters = JobParametersBuilder()
                .addString("targetDate", yesterdayStr)
                // runId를 매번 다르게 주어, 실패 후 재시도나 매일 실행이 가능하게 함
                .addString("runId", LocalDateTime.now().toString())
                .toJobParameters()

            jobLauncher.run(dailyStatisticsJob, jobParameters)
        } catch (e: Exception) {
            log.error(e) { "Failed to run DailyStatisticsJob for date: $yesterdayStr" }
            // 추후 슬랙/이메일 알림 연동 가능
        }
    }
}