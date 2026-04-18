package com.contentria.batch.jobs.statistics

import com.contentria.batch.global.config.TestContainerConfig
import com.contentria.common.domain.analytics.VisitLog
import com.contentria.common.domain.analytics.repository.DailyStatisticsRepository
import com.contentria.common.domain.analytics.repository.VisitLogRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.batch.core.job.parameters.JobParametersBuilder
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@Import(TestContainerConfig::class)
@SpringBatchTest
@SpringBootTest
class DailyStatisticsJobTest(
    @param:Autowired private val jobLauncherTestUtils: JobLauncherTestUtils,
    @param:Autowired private val visitLogRepository: VisitLogRepository,
    @param:Autowired private val dailyStatisticsRepository: DailyStatisticsRepository
) {

    @BeforeEach
    fun setUp() {
        dailyStatisticsRepository.deleteAll()
        visitLogRepository.deleteAll()
    }

    @Test
    @DisplayName("방문 로그가 존재할 때, 배치가 실행되면 게시글별 및 블로그 전체 통계(PV, UV)가 정확히 집계되어야 한다")
    fun should_CreateAggregatedStatistics_when_VisitLogsExist() {
        // Given
        val targetDate = LocalDate.now().minusDays(1)
        val blogId = UUID.randomUUID()
        val postId01 = UUID.randomUUID()
        val postId02 = UUID.randomUUID()

        val zoneId = ZoneId.of("Asia/Seoul")
        createAndSaveLog(blogId, postId01, "1.1.1.1", targetDate.atTime(10, 0).atZone(zoneId))
        createAndSaveLog(blogId, postId01, "1.1.1.1", targetDate.atTime(11, 0).atZone(zoneId))
        createAndSaveLog(blogId, postId01, "2.2.2.2", targetDate.atTime(12, 0).atZone(zoneId))
        createAndSaveLog(blogId, postId02, "3.3.3.3", targetDate.atTime(13, 0).atZone(zoneId))

        val jobParameters = JobParametersBuilder()
            .addString("targetDate", targetDate.toString())
            .addString("runId", UUID.randomUUID().toString())
            .toJobParameters()

        // When
        val jobExecution = jobLauncherTestUtils.launchJob(jobParameters)

        // Then
        assertThat(jobExecution.exitStatus.exitCode).isEqualTo("COMPLETED")

        val stats = dailyStatisticsRepository.findAll()
        assertThat(stats).hasSize(3)

        val postStat = stats.first { it.postId == postId01 }
        assertThat(postStat.visitCount).isEqualTo(2L)
        assertThat(postStat.viewCount).isEqualTo(3L)

        val blogStat = stats.first { it.postId == null }
        assertThat(blogStat.visitCount).isEqualTo(3L)
        assertThat(blogStat.viewCount).isEqualTo(4L)
    }

    private fun createAndSaveLog(blogId: UUID, postId: UUID, ip: String, visitedAt: ZonedDateTime) {
        visitLogRepository.save(
            VisitLog(
                blogId = blogId,
                postId = postId,
                visitorIp = ip,
                visitedAt = visitedAt,
                userAgent = "TestAgent",
                refererUrl = null
            )
        )
    }
}