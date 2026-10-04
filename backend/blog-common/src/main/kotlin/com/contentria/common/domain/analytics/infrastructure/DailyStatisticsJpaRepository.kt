package com.contentria.common.domain.analytics.infrastructure

import com.contentria.common.domain.analytics.DailyStatistics
import com.contentria.common.domain.analytics.PopularPostStatProjection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.time.ZonedDateTime
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
        SELECT SUM(d.viewCount)
        FROM DailyStatistics d
        WHERE d.blogId = :blogId
            AND d.postId IS NULL
    """)
    fun sumTotalViews(blogId: UUID): Long?

    @Query("""
        SELECT SUM(d.visitCount)
        FROM DailyStatistics d
        WHERE d.blogId = :blogId
            AND d.postId IS NULL
    """)
    fun sumTotalVisitors(blogId: UUID): Long?

    @Query("""
        SELECT d
        FROM DailyStatistics d
        WHERE d.blogId = :blogId
            AND d.postId IS NULL
            AND d.statDate BETWEEN :startDate AND :endDate
        ORDER BY d.statDate ASC
    """)
    fun findTrafficData(blogId: UUID, startDate: LocalDate, endDate: LocalDate): List<DailyStatistics>

    /**
     * 인기 게시글: 배치가 집계한 daily_statistics(어제까지)와 visit_logs의 오늘 라이브
     * 카운트를 UNION으로 합산한다 — 배치가 아직 안 돈 가입 첫날에도 카드가 채워지고,
     * 오늘 조회수가 하루 늦게 반영되는 문제가 사라진다. 정렬·limit은 합산 후 적용.
     * (호출부가 endDate를 어제로 제한해 수동 배치로 오늘 행이 있어도 중복 집계되지 않는다)
     */
    @Query(
        value = """
            SELECT
                CAST(s.post_id AS VARCHAR) as postId,
                p.title as title,
                p.slug as slug,
                SUM(s.view_count) as viewCount
            FROM (
                SELECT d.post_id AS post_id, d.view_count AS view_count
                FROM daily_statistics d
                WHERE d.blog_id = :blogId
                    AND d.post_id IS NOT NULL
                    AND d.stat_date BETWEEN :startDate AND :endDate
                UNION ALL
                SELECT v.post_id AS post_id, COUNT(*) AS view_count
                FROM visit_logs v
                WHERE v.blog_id = :blogId
                    AND v.post_id IS NOT NULL
                    AND v.visited_at >= :liveSince
                GROUP BY v.post_id
            ) s
            JOIN posts p ON s.post_id = p.id
            GROUP BY s.post_id, p.title, p.slug
            ORDER BY viewCount DESC
        """,
        nativeQuery = true
    )
    fun findPopularPosts(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        liveSince: ZonedDateTime,
        pageable: Pageable
    ): List<PopularPostStatProjection>

    fun findByBlogIdAndStatDateAndPostIdIsNull(blogId: UUID, statDate: LocalDate): DailyStatistics?
}