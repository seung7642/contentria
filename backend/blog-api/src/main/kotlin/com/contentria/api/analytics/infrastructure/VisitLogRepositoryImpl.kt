package com.contentria.api.analytics.infrastructure

import com.contentria.api.analytics.domain.VisitLog
import com.contentria.api.analytics.domain.VisitLogRepository
import org.springframework.stereotype.Repository
import java.util.UUID

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
}