package com.contentria.api.analytics.application

import com.contentria.api.analytics.application.dto.PopularPostStatInfo
import com.contentria.api.analytics.application.dto.TrafficDataInfo
import com.contentria.api.analytics.application.dto.VisitStatsInfo
import com.contentria.api.analytics.domain.DailyStatisticsRepository
import com.contentria.api.analytics.domain.VisitLogRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AnalyticsService(
    private val visitLogRepository: VisitLogRepository,
    private val dailyStatisticsRepository: DailyStatisticsRepository
) {
    fun getVisitStats(blogId: UUID): VisitStatsInfo {
        val today = LocalDate.now()
        val startOfToday = today.atStartOfDay(ZoneId.of("Asia/Seoul"))

        val todayVisitors = visitLogRepository.countTodayVisitors(blogId, startOfToday)

        val startOfWeek = today.minusDays(6)
        val yesterday = today.minusDays(1)

        val pastWeekVisitors = dailyStatisticsRepository.sumVisitorBetween(blogId, startOfWeek, yesterday) ?: 0L
        
        return VisitStatsInfo(
            todayVisitors = todayVisitors,
            weekVisitors = pastWeekVisitors + todayVisitors
        )
    }

    fun getTrafficData(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        formatter: DateTimeFormatter
    ): List<TrafficDataInfo> {
        val dbData = dailyStatisticsRepository.findTrafficData(blogId, startDate, endDate)
        val statsMap = dbData.associateBy { it.statDate }

        val today = LocalDate.now()
        val result = mutableListOf<TrafficDataInfo>()

        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val dateStr = currentDate.format(formatter)

            val visitors = if (currentDate.isEqual(today)) {
                visitLogRepository.countTodayVisitors(blogId, today.atStartOfDay(ZoneId.of("Asia/Seoul")))
            } else {
                statsMap[currentDate]?.visitCount ?: 0L
            }

            result.add(TrafficDataInfo(date = dateStr, visitors = visitors))
            currentDate = currentDate.plusDays(1)
        }

        return result
    }

    fun getPopularPosts(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        limit: Int
    ): List<PopularPostStatInfo> {
        val pageable = PageRequest.of(0, limit)

        return dailyStatisticsRepository.findPopularPosts(blogId, startDate, endDate, pageable)
            .map {
                PopularPostStatInfo(
                    postId = it.getPostId(),
                    title = it.getTitle(),
                    viewCount = it.getViewCount()
                )
            }
    }
}