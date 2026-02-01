package com.contentria.api.analytics.application

import com.contentria.api.analytics.application.dto.PopularPostStatInfo
import com.contentria.api.analytics.application.dto.VisitorTrendInfo
import com.contentria.api.analytics.application.dto.VisitStatsInfo
import com.contentria.api.analytics.domain.DailyStatisticsRepository
import com.contentria.api.analytics.domain.StatisticsCalculator
import com.contentria.api.analytics.domain.VisitLogRepository
import com.contentria.api.analytics.domain.VisitorTrendProcessor
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class AnalyticsService(
    private val visitLogRepository: VisitLogRepository,
    private val dailyStatisticsRepository: DailyStatisticsRepository,
    private val calculator: StatisticsCalculator,
    private val visitorTrendProcessor: VisitorTrendProcessor
) {
    fun getVisitStats(blogId: UUID): VisitStatsInfo {
        val (todayVisitors, todayViews) = fetchTodayMetrics(blogId)
        val (yesterdayVisitors, yesterdayViews) = fetchYesterdayMetrics(blogId)
        val historyTotalViews = dailyStatisticsRepository.sumTotalViews(blogId)
        val totalViews = calculator.calculateTotalViews(historyTotalViews, todayViews)

        val todayVisitorsGrowthRate = calculator.calculateGrowthRate(todayVisitors, yesterdayVisitors)
        val todayViewsGrowthRate = calculator.calculateGrowthRate(todayViews, yesterdayViews)

        return VisitStatsInfo(
            todayVisitors = todayVisitors,
            todayViews = todayViews,
            todayVisitorsGrowthRate = todayVisitorsGrowthRate,
            todayViewsGrowthRate = todayViewsGrowthRate,
            yesterdayVisitors = yesterdayVisitors,
            yesterdayViews = yesterdayViews,
            totalViews = totalViews
        )
    }

    private fun fetchTodayMetrics(blogId: UUID): Pair<Long, Long> {
        val startOfToday = LocalDate.now().atStartOfDay(ZoneId.of("Asia/Seoul"))
        val visitors = visitLogRepository.countTodayVisitors(blogId, startOfToday)
        val views = visitLogRepository.countTodayViews(blogId, startOfToday)
        return Pair(visitors, views)
    }

    private fun fetchYesterdayMetrics(blogId: UUID): Pair<Long, Long> {
        val yesterday = LocalDate.now().minusDays(1)
        val stats = dailyStatisticsRepository.findByBlogIdAndStatDateAndPostIdIsNull(blogId, yesterday)
        return Pair(
            stats?.visitCount ?: 0L,
            stats?.viewCount ?: 0L
        )
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

    fun getVisitorTrend(
        blogId: UUID,
        startDate: LocalDate,
        endDate: LocalDate,
        formatter: DateTimeFormatter
    ): List<VisitorTrendInfo> {
        val historicalDailyStats = dailyStatisticsRepository.findTrafficData(blogId, startDate, endDate)
        val historyStatsMap = historicalDailyStats.associateBy { it.statDate }

        val zoneId = ZoneId.of("Asia/Seoul")
        val today = LocalDate.now(zoneId)
        val isTodayIncluded = !today.isBefore(startDate) && !today.isAfter(endDate)

        val todayVisitors = if (isTodayIncluded) {
            visitLogRepository.countTodayVisitors(blogId, today.atStartOfDay(zoneId))
        } else {
            0L
        }

        val trendSeriesMap = visitorTrendProcessor.generateTrendSeries(
            startDate = startDate,
            endDate = endDate,
            historyStatsMap = historyStatsMap,
            todayVisitors = todayVisitors,
        )

        return trendSeriesMap.map { (date, count) ->
            VisitorTrendInfo(
                date = date.format(formatter),
                count = count
            )
        }
    }
}