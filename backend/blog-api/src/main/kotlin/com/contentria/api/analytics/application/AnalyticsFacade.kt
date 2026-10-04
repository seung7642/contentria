package com.contentria.api.analytics.application

import com.contentria.api.analytics.application.dto.LogVisitCommand
import com.contentria.api.blog.application.BlogService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
class AnalyticsFacade(
    private val analyticsInternalService: AnalyticsInternalService,
    private val blogService: BlogService
) {
    fun trackVisit(command: LogVisitCommand) {
        // 블로그 소유자 본인의 방문은 통계에서 제외한다 (티스토리 등 일반 관행)
        if (command.viewerId != null && blogService.getBlogInfo(command.blogId).userId == command.viewerId) {
            log.debug { "Skipping visit log: blog owner's own visit (blogId=${command.blogId})" }
            return
        }

        analyticsInternalService.logVisit(command)
    }
}
