package com.contentria.api.analytics.application

import com.contentria.api.analytics.application.dto.LogVisitCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger {  }

@Component
class AnalyticsFacade(
    private val analyticsInternalService: AnalyticsInternalService
) {
    fun trackVisit(command: LogVisitCommand) {
        analyticsInternalService.logVisit(command)
    }
}