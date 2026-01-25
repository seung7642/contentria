package com.contentria.api.analytics.application.dto

import java.util.UUID

data class LogVisitCommand(
    val blogId: UUID,
    val postId: UUID?,
    val visitorIp: String?,
    val userAgent: String?,
    val refererUrl: String?
)