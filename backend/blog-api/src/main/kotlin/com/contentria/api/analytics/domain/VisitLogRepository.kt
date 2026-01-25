package com.contentria.api.analytics.domain

import java.util.*

interface VisitLogRepository {

    fun findById(id: UUID): VisitLog?
    fun save(visitLog: VisitLog): VisitLog
    fun delete(visitLog: VisitLog)
    fun deleteAll(visitLogs: List<VisitLog>)
}