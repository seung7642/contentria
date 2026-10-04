package com.contentria.batch.jobs.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.job.Job
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.core.launch.JobOperator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.Timestamp
import java.time.Instant
import java.util.*

//@Import(TestContainerConfig::class)
//@SpringBootTest
class RefreshTokenCleanupJobTest(
    @param:Autowired private val jobOperator: JobOperator,
    @param:Autowired @param:Qualifier("refreshTokenCleanupJob") private val cleanupJob: Job,
    @param:Autowired private val jdbcTemplate: JdbcTemplate
) {

    @BeforeEach
    fun setUp() {
        // The `refresh_tokens` table is owned by blog-api; blog-batch does not depend on it
        // for compilation, so the JPA ddl-auto pass in tests never creates the table. Create
        // a minimal mirror here — only the columns the cleanup job touches matter.
        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS refresh_tokens (
                id UUID PRIMARY KEY,
                token VARCHAR(512) NOT NULL UNIQUE,
                expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
                user_id UUID NOT NULL,
                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """.trimIndent()
        )
        jdbcTemplate.update("DELETE FROM refresh_tokens")
    }

//    @Test
    @DisplayName("배치가 실행되면 만료된 refresh token만 삭제되고, 유효한 토큰은 보존된다")
    fun should_DeleteOnlyExpiredTokens_when_JobRuns() {
        // Given
        val userId = UUID.randomUUID()
        val now = Instant.now()
        insertToken(userId, now.minusSeconds(3600))  // expired 1h ago
        insertToken(userId, now.minusSeconds(1))     // expired 1s ago
        insertToken(userId, now.plusSeconds(3600))   // valid 1h
        insertToken(userId, now.plusSeconds(86400))  // valid 1d

        val jobParameters = JobParametersBuilder()
            .addString("runId", UUID.randomUUID().toString())
            .toJobParameters()

        // When
        val jobExecution = jobOperator.start(cleanupJob, jobParameters)

        // Then
        assertThat(jobExecution.exitStatus.exitCode).isEqualTo("COMPLETED")

        val remaining = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM refresh_tokens", Int::class.java
        )
        assertThat(remaining).isEqualTo(2)

        val deletedCount = jobExecution.stepExecutions.sumOf { it.writeCount }
        assertThat(deletedCount).isEqualTo(2L)
    }

    private fun insertToken(userId: UUID, expiryDate: Instant) {
        jdbcTemplate.update(
            """
            INSERT INTO refresh_tokens (id, token, expiry_date, user_id, created_at, updated_at)
            VALUES (?, ?, ?, ?, now(), now())
            """.trimIndent(),
            UUID.randomUUID(),
            UUID.randomUUID().toString(),
            Timestamp.from(expiryDate),
            userId
        )
    }
}
