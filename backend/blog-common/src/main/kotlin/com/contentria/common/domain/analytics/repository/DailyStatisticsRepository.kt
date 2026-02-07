package com.contentria.common.domain.analytics.repository

import com.contentria.common.domain.analytics.DailyStatistics
import com.contentria.common.domain.analytics.PopularPostStatProjection
import org.springframework.data.domain.Pageable
import java.time.LocalDate
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

    fun findTrafficData(blogId: UUID, startDate: LocalDate, endDate: LocalDate): List<DailyStatistics>

    fun findPopularPosts(blogId: UUID, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): List<PopularPostStatProjection>

    fun findByBlogIdAndStatDateAndPostIdIsNull(blogId: UUID, statDate: LocalDate): DailyStatistics?
}