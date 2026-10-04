package com.contentria.batch.jobs.statistics

import java.util.UUID

data class DailyStatisticsDto(
    val blogId: UUID,
    val postId: UUID?,
    val visitCount: Long,
    val viewCount: Long
)