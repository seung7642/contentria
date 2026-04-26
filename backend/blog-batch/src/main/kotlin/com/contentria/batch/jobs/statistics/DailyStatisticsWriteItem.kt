package com.contentria.batch.jobs.statistics

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.SqlParameterSource
import java.time.LocalDate

data class DailyStatisticsWriteItem(
    val blogId: java.util.UUID,
    val postId: java.util.UUID?,
    val statDate: LocalDate,
    val visitCount: Long,
    val viewCount: Long
) {
    fun toParameterSource(): SqlParameterSource =
        MapSqlParameterSource()
            .addValue("blogId", blogId)
            .addValue("postId", postId)
            .addValue("statDate", statDate)
            .addValue("visitCount", visitCount)
            .addValue("viewCount", viewCount)
}
