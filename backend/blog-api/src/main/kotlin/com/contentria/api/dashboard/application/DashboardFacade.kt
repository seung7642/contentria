package com.contentria.api.dashboard.application

import com.contentria.api.analytics.application.AnalyticsService
import com.contentria.api.blog.application.BlogService
import com.contentria.api.blog.application.dto.BlogInfo
import com.contentria.api.dashboard.application.dto.DashboardStatsInfo
import com.contentria.api.dashboard.application.dto.PopularPostInfo
import com.contentria.api.dashboard.application.dto.VisitorTrendChartInfo
import com.contentria.api.dashboard.dto.TimeRange
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.post.application.PostService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

private val log = KotlinLogging.logger {  }

@Service
class DashboardFacade(
    private val blogService: BlogService,
    private val postService: PostService,
    private val analyticsService: AnalyticsService
) {
    fun getStats(ownerId: UUID, blogSlug: String): DashboardStatsInfo {
        val blogInfo = blogService.getBlogInfo(blogSlug)
        validateOwner(ownerId, blogInfo)

        val totalPosts = postService.countPublishedPosts(blogInfo.blogId)

        val visitStats = analyticsService.getVisitStats(blogInfo.blogId)

        return DashboardStatsInfo(
            todayVisitors = visitStats.todayVisitors,
            todayVisitorsGrowthRate = visitStats.todayVisitorsGrowthRate,
            todayViews = visitStats.todayViews,
            todayViewsGrowthRate = visitStats.todayViewsGrowthRate,
            totalViews = visitStats.totalViews,
            totalPosts = totalPosts,
        )
    }

    fun getPopularPosts(ownerId: UUID, blogSlug: String, limit: Int): List<PopularPostInfo> {
        val blogInfo = blogService.getBlogInfo(blogSlug)
        validateOwner(ownerId, blogInfo)

        val today = LocalDate.now()
        val startDate = today.minusDays(29)

        val popularPosts = analyticsService.getPopularPosts(
            blogId = blogInfo.blogId,
            startDate = startDate,
            endDate = today,
            limit = limit
        )

        return popularPosts.map {
            PopularPostInfo(
                id = it.postId,
                title = it.title,
                views = it.viewCount
            )
        }
    }

    fun getVisitorTrend(ownerId: UUID, blogSlug: String, timeRange: TimeRange): List<VisitorTrendChartInfo> {
        val blogInfo = blogService.getBlogInfo(blogSlug)
        validateOwner(ownerId, blogInfo)

        val today = LocalDate.now(ZoneId.of("Asia/Seoul"))

        val (startDate, formatter) = when (timeRange) {
            TimeRange.TWO_WEEKS -> today.minusDays(13) to DateTimeFormatter.ofPattern("MM/dd")
            TimeRange.THIRTY_DAYS -> today.minusDays(29) to DateTimeFormatter.ofPattern("MM/dd")
            TimeRange.NINETY_DAYS -> today.minusMonths(3) to DateTimeFormatter.ofPattern("yyyy/MM")
        }

        val trendSeries = analyticsService.getVisitorTrend(
            blogId = blogInfo.blogId,
            startDate = startDate,
            endDate = today,
            formatter = formatter
        )

        return trendSeries.map {
            VisitorTrendChartInfo(
                date = it.date,
                visitors = it.count
            )
        }
    }

    private fun validateOwner(ownerId: UUID, blogInfo: BlogInfo) {
        if (blogInfo.userId != ownerId) {
            throw ContentriaException(ErrorCode.FORBIDDEN_ACCESS_BLOG)
        }
    }
}