package com.contentria.api.analytics.infrastructure

import com.contentria.api.analytics.domain.VisitLog
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface VisitLogJpaRepository : JpaRepository<VisitLog, UUID> {

}