package com.contentria.common.domain.analytics

import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

interface DailyStatisticsRepository {

    fun findAll(): List<DailyStatistics>
    fun findById(id: UUID): DailyStatistics?
    fun save(dailyStatistics: DailyStatistics): DailyStatistics
    fun delete(dailyStatistics: DailyStatistics)
    fun deleteAll(dailyStatistics: List<DailyStatistics>)
    fun deleteAll()

    fun sumVisitorBetween(blogId: UUID, startDate: LocalDate, endDate: LocalDate): Long?

    fun sumTotalViews(blogId: UUID): Long?

    fun sumTotalVisitors(blogId: UUID): Long?

    fun findTrafficData(blogId: UUID, startDate: LocalDate, endDate: LocalDate): List<DailyStatistics>

    /** 히스토리(daily_statistics, startDate..endDate) + 오늘 라이브(visit_logs, liveSince 이후) 합산 */
    fun findPopularPosts(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        liveSince: ZonedDateTime,
        pageable: Pageable
    ): List<PopularPostStatProjection>

    fun findByBlogIdAndStatDateAndPostIdIsNull(blogId: UUID, statDate: LocalDate): DailyStatistics?
}