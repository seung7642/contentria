package com.contentria.batch.jobs.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.Step
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.infrastructure.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.PlatformTransactionManager

private val log = KotlinLogging.logger {}

/**
 * Deletes expired refresh tokens. A Tasklet (not chunk-oriented) suits a single DML statement.
 *
 * Complements the opportunistic deletion in `RefreshTokenService.rotate` (which only fires on
 * use) so that abandoned tokens — e.g. from devices the user never returns to — do not
 * accumulate indefinitely.
 */
@Configuration
class RefreshTokenCleanupJobConfig(
    private val jdbcTemplate: JdbcTemplate
) {

    @Bean
    fun refreshTokenCleanupJob(
        jobRepository: JobRepository,
        refreshTokenCleanupStep: Step
    ): Job {
        return JobBuilder(JOB_NAME, jobRepository)
            .start(refreshTokenCleanupStep)
            .build()
    }

    @Bean
    fun refreshTokenCleanupStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager
    ): Step {
        return StepBuilder(STEP_NAME, jobRepository)
            .tasklet({ contribution, _ ->
                val deleted = jdbcTemplate.update(
                    "DELETE FROM refresh_tokens WHERE expiry_date < now()"
                )
                contribution.incrementWriteCount(deleted.toLong())
                log.info { "Deleted $deleted expired refresh token(s)." }
                RepeatStatus.FINISHED
            }, transactionManager)
            .build()
    }

    companion object {
        const val JOB_NAME = "refreshTokenCleanupJob"
        const val STEP_NAME = "refreshTokenCleanupStep"
    }
}
