package com.contentria.common.domain.analytics.repository

import com.contentria.common.domain.analytics.VisitLog
import java.time.ZonedDateTime
import java.util.*

interface VisitLogRepository {

    fun findById(id: UUID): VisitLog?
    fun save(visitLog: VisitLog): VisitLog
    fun delete(visitLog: VisitLog)
    fun deleteAll(visitLogs: List<VisitLog>)
    fun deleteAll()

    fun countTodayVisitors(blogId: UUID, startOfToday: ZonedDateTime): Long

    fun countTodayViews(blogId: UUID, startOfDay: ZonedDateTime): Long

    fun existsRecentVisit(
        blogId: UUID,
        postId: UUID?,
        visitorIp: String,
        since: ZonedDateTime
    ): Boolean
}