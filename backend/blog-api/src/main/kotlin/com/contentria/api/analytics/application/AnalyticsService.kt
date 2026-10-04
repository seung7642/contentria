package com.contentria.api.analytics.application

import com.contentria.api.analytics.application.dto.PopularPostStatInfo
import com.contentria.api.analytics.application.dto.VisitorTrendInfo
import com.contentria.api.analytics.application.dto.VisitStatsInfo
import com.contentria.common.domain.analytics.StatisticsCalculator
import com.contentria.common.domain.analytics.VisitorTrendSeriesGenerator
import com.contentria.common.domain.analytics.DailyStatisticsRepository
import com.contentria.common.domain.analytics.VisitLogRepository
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
    private val visitorTrendSeriesGenerator: VisitorTrendSeriesGenerator
) {
    fun getVisitStats(blogId: UUID): VisitStatsInfo {
        val (todayVisitors, todayViews) = fetchTodayMetrics(blogId)
        val (yesterdayVisitors, yesterdayViews) = fetchYesterdayMetrics(blogId)

        // 누적 = 배치 집계분(어제까지) + 오늘 라이브
        val totalViews = calculator.calculateTotalViews(
            dailyStatisticsRepository.sumTotalViews(blogId), todayViews
        )
        val totalVisitors = calculator.calculateTotalViews(
            dailyStatisticsRepository.sumTotalVisitors(blogId), todayVisitors
        )

        return VisitStatsInfo(
            todayVisitors = todayVisitors,
            yesterdayVisitors = yesterdayVisitors,
            totalVisitors = totalVisitors,
            todayViews = todayViews,
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

        val zoneId = ZoneId.of("Asia/Seoul")
        val today = LocalDate.now(zoneId)
        val isTodayIncluded = !today.isBefore(startDate) && !today.isAfter(endDate)

        // 오늘은 daily_statistics에 아직 없으므로 visit_logs 라이브 카운트를 합산한다.
        // 히스토리 범위는 어제까지로 제한해 (수동 배치 등으로) 오늘 행이 이미 있어도
        // 중복 집계되지 않게 한다. 범위에 오늘이 없으면 라이브 분기는 0건이 되도록
        // 내일 0시를 기준으로 보낸다.
        val historyEnd = minOf(endDate, today.minusDays(1))
        val liveSince = (if (isTodayIncluded) today else today.plusDays(1)).atStartOfDay(zoneId)

        return dailyStatisticsRepository.findPopularPosts(blogId, startDate, historyEnd, liveSince, pageable)
            .map {
                PopularPostStatInfo(
                    postId = it.getPostId(),
                    title = it.getTitle(),
                    slug = it.getSlug(),
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

        val (todayVisitors, todayViews) = if (isTodayIncluded) {
            val startOfToday = today.atStartOfDay(zoneId)
            visitLogRepository.countTodayVisitors(blogId, startOfToday) to
                visitLogRepository.countTodayViews(blogId, startOfToday)
        } else {
            0L to 0L
        }

        val trendSeriesMap = visitorTrendSeriesGenerator.generateTrendSeries(
            startDate = startDate,
            endDate = endDate,
            historyStatsMap = historyStatsMap,
            todayVisitors = todayVisitors,
            todayViews = todayViews,
        )

        return trendSeriesMap.map { (date, point) ->
            VisitorTrendInfo(
                date = date.format(formatter),
                visitors = point.visitors,
                views = point.views
            )
        }
    }
}