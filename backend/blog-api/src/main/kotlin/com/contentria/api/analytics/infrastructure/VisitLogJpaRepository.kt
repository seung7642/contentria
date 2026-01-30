package com.contentria.api.analytics.infrastructure

import com.contentria.api.analytics.domain.VisitLog
import org.springframework.data.jpa.repository.JpaRepository
import java.time.ZonedDateTime
import java.util.UUID

interface VisitLogJpaRepository : JpaRepository<VisitLog, UUID> {


    fun countByBlogIdAndVisitedAtAfter(blogId: UUID, startOfToday: ZonedDateTime): Long

}