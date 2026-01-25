package com.contentria.api.analytics.controller.dto

import com.contentria.api.analytics.application.dto.LogVisitCommand
import java.util.UUID

data class TrackVisitRequest(
    val blogId: UUID,
    val postId: UUID?,
    val refererUrl: String?
) {
    fun toCommand(clientIp: String?, userAgent: String?): LogVisitCommand {
        return LogVisitCommand(
            blogId = this.blogId,
            postId = this.postId,
            visitorIp = clientIp,
            userAgent = userAgent,
            refererUrl = this.refererUrl
        )
    }
}
