package com.contentria.api.analytics.application

import com.contentria.api.analytics.application.dto.LogVisitCommand
import com.contentria.common.domain.analytics.VisitLog
import com.contentria.common.domain.analytics.repository.VisitLogRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.ZonedDateTime

private val log = KotlinLogging.logger {  }

@Service
class AnalyticsInternalService(
    private val visitLogRepository: VisitLogRepository
) {
    @Async
    @Transactional
    fun logVisit(command: LogVisitCommand) {
        try {
            if (shouldSkipAsDuplicate(command)) {
                log.debug { "Skipping visit log within cooldown window." }
                return
            }
            val visitLog = VisitLog.create(
                blogId = command.blogId,
                postId = command.postId,
                visitorIp = command.visitorIp,
                userAgent = command.userAgent,
                refererUrl = command.refererUrl
            )
            visitLogRepository.save(visitLog)
            log.debug { "Successfully saved a visit log." }
        } catch (e: Exception) {
            log.error(e) { "Failed to save visit log async" }
        }
    }

    private fun shouldSkipAsDuplicate(command: LogVisitCommand): Boolean {
        val ip = command.visitorIp ?: return false
        val since = ZonedDateTime.now().minus(PV_COOLDOWN)
        return visitLogRepository.existsRecentVisit(
            blogId = command.blogId,
            postId = command.postId,
            visitorIp = ip,
            since = since
        )
    }

    companion object {
        private val PV_COOLDOWN: Duration = Duration.ofMinutes(30)
    }
}