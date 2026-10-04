package com.contentria.api.post.domain.query

import java.time.ZonedDateTime
import java.util.UUID

/** 홈 화면 "최근 발행된 글" 섹션용 — 전체 블로그를 가로지르는 최신 공개 글 프로젝션. */
data class RecentPublishedPost(
    val postId: UUID,
    val title: String,
    val summary: String,
    val postSlug: String,
    val blogSlug: String,
    val authorNickname: String,
    val publishedAt: ZonedDateTime?
)
