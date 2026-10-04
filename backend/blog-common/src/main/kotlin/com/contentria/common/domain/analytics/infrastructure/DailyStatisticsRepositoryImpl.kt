package com.contentria.common.domain.analytics.infrastructure

import com.contentria.common.domain.analytics.DailyStatistics
import com.contentria.common.domain.analytics.DailyStatisticsRepository
import com.contentria.common.domain.analytics.PopularPostStatProjection
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

@Repository
class DailyStatisticsRepositoryImpl(
    private val dailyStatisticsJpaRepository: DailyStatisticsJpaRepository
) : DailyStatisticsRepository {

    override fun findAll(): List<DailyStatistics> {
        return dailyStatisticsJpaRepository.findAll()
    }

    override fun findById(id: UUID): DailyStatistics? {
        return dailyStatisticsJpaRepository.findById(id).orElse(null)
    }

    override fun save(dailyStatistics: DailyStatistics): DailyStatistics {
        return dailyStatisticsJpaRepository.save(dailyStatistics)
    }

    override fun delete(dailyStatistics: DailyStatistics) {
        dailyStatisticsJpaRepository.delete(dailyStatistics)
    }

    override fun deleteAll(dailyStatistics: List<DailyStatistics>) {
        dailyStatisticsJpaRepository.deleteAll(dailyStatistics)
    }

    override fun deleteAll() {
        dailyStatisticsJpaRepository.deleteAll()
    }

    override fun sumVisitorBetween(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): Long? {
        return dailyStatisticsJpaRepository.sumVisitorsBetween(blogId, startDate, endDate)
    }

    override fun sumTotalViews(blogId: UUID): Long? {
        return dailyStatisticsJpaRepository.sumTotalViews(blogId)
    }

    override fun sumTotalVisitors(blogId: UUID): Long? {
        return dailyStatisticsJpaRepository.sumTotalVisitors(blogId)
    }

    override fun findTrafficData(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailyStatistics> {
        return dailyStatisticsJpaRepository.findTrafficData(blogId, startDate, endDate)
    }

    override fun findPopularPosts(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        liveSince: ZonedDateTime,
        pageable: Pageable
    ): List<PopularPostStatProjection> {
        return dailyStatisticsJpaRepository.findPopularPosts(blogId, startDate, endDate, liveSince, pageable)
    }

    override fun findByBlogIdAndStatDateAndPostIdIsNull(
        blogId: UUID,
        statDate: LocalDate
    ): DailyStatistics? {
        return dailyStatisticsJpaRepository.findByBlogIdAndStatDateAndPostIdIsNull(blogId, statDate)
    }
}