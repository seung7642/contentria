package com.contentria.common.domain.analytics.repository

import com.contentria.common.domain.analytics.VisitLog
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.*

@Repository
class VisitLogRepositoryImpl(
    private val jpaRepository: VisitLogJpaRepository
) : VisitLogRepository {

    override fun findById(id: UUID): VisitLog? {
        return jpaRepository.findById(id).orElse(null)
    }

    override fun save(visitLog: VisitLog): VisitLog {
        return jpaRepository.save(visitLog)
    }

    override fun delete(visitLog: VisitLog) {
        jpaRepository.delete(visitLog)
    }

    override fun deleteAll(visitLogs: List<VisitLog>) {
        jpaRepository.deleteAll(visitLogs)
    }

    override fun deleteAll() {
        jpaRepository.deleteAll()
    }

    override fun countTodayVisitors(blogId: UUID, startOfToday: ZonedDateTime): Long {
        return jpaRepository.countTodayVisitors(blogId, startOfToday)
    }

    override fun countTodayViews(blogId: UUID, startOfDay: ZonedDateTime): Long {
        return jpaRepository.countTodayViews(blogId, startOfDay)
    }
}