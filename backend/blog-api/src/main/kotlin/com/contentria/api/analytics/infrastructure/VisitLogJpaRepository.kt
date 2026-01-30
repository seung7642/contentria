package com.contentria.api.analytics.infrastructure

import com.contentria.api.analytics.domain.VisitLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.ZonedDateTime
import java.util.UUID

interface VisitLogJpaRepository : JpaRepository<VisitLog, UUID> {

    @Query("""
        SELECT COUNT(DISTINCT v.visitorIp)
        FROM VisitLog v 
        WHERE v.blogId = :blogId
        AND v.visitedAt >= :startOfToday 
    """)
    fun countTodayVisitors(blogId: UUID, startOfToday: ZonedDateTime): Long

    @Query("""
        SELECT COUNT(v)
        FROM VisitLog v
        WHERE v.blogId = :blogId
        AND v.visitedAt >= :startOfDay
    """)
    fun countTodayViews(blogId: UUID, startOfDay: ZonedDateTime): Long
}