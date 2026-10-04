package com.contentria.api.analytics.application.dto

import java.util.UUID

data class LogVisitCommand(
    val blogId: UUID,
    val postId: UUID?,
    val visitorIp: String?,
    val userAgent: String?,
    val refererUrl: String?,
    /** 로그인 상태로 방문한 사용자 — 블로그 소유자 본인 방문 제외 판정에 사용 (익명이면 null) */
    val viewerId: UUID? = null
)
