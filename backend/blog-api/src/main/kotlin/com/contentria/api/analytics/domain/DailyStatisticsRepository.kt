package com.contentria.api.analytics.domain

import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

interface DailyStatisticsRepository {

    fun findById(id: UUID): DailyStatistics?
    fun save(dailyStatistics: DailyStatistics): DailyStatistics
    fun delete(dailyStatistics: DailyStatistics)
    fun deleteAll(dailyStatistics: List<DailyStatistics>)

    fun sumVisitorBetween(blogId: UUID, startDate: LocalDate, endDate: LocalDate): Long?

    fun sumTotalViews(blogId: UUID): Long?

    fun findTrafficData(blogId: UUID, startDate: LocalDate, endDate: LocalDate): List<DailyStatistics>

    fun findPopularPosts(blogId: UUID, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): List<PopularPostStatProjection>

    fun findByBlogIdAndStatDateAndPostIdIsNull(blogId: UUID, statDate: LocalDate): DailyStatistics?
}