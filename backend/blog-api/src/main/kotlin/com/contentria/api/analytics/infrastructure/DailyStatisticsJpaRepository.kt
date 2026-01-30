package com.contentria.api.analytics.infrastructure

import com.contentria.api.analytics.domain.DailyStatistics
import com.contentria.api.analytics.domain.PopularPostStatProjection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface DailyStatisticsJpaRepository : JpaRepository<DailyStatistics, UUID> {

    @Query("""
        SELECT SUM(d.visitCount)
        FROM DailyStatistics d
        WHERE d.blogId = :blogId
            AND d.postId IS NULL
            AND d.statDate BETWEEN :startDate AND :endDate
    """)
    fun sumVisitorsBetween(blogId: UUID, startDate: LocalDate, endDate: LocalDate): Long?

    @Query("""
        SELECT d
        FROM DailyStatistics d
        WHERE d.blogId = :blogId
            AND d.postId IS NULL
            AND d.statDate BETWEEN :startDate AND :endDate
        ORDER BY d.statDate ASC
    """)
    fun findTrafficData(blogId: UUID, startDate: LocalDate, endDate: LocalDate): List<DailyStatistics>

    @Query(
        value = """
            SELECT
                CAST(d.post_id AS VARCHAR) as postId,
                p.title as title,
                SUM(d.view_count) as viewCount
            FROM daily_statistics d
            JOIN posts p ON d.post_id = p.id
            WHERE d.blog_id = :blogId
                AND d.post_id IS NOT NULL
                AND d.stat_date BETWEEN :startDate AND :endDate
            GROUP BY d.post_id, p.title
            ORDER BY viewCount DESC
        """,
        nativeQuery = true
    )
    fun findPopularPosts(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): List<PopularPostStatProjection>
}