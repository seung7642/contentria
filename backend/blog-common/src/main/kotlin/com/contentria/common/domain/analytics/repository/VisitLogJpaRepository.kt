package com.contentria.common.domain.analytics.repository

import com.contentria.common.domain.analytics.VisitLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.ZonedDateTime
import java.util.*

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