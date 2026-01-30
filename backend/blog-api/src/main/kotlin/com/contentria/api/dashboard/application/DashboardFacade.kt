package com.contentria.api.dashboard.application

import com.contentria.api.analytics.application.AnalyticsService
import com.contentria.api.blog.application.BlogService
import com.contentria.api.dashboard.application.dto.DashboardStatsInfo
import com.contentria.api.dashboard.application.dto.PopularPostInfo
import com.contentria.api.dashboard.application.dto.TrafficChartInfo
import com.contentria.api.dashboard.dto.TimeRange
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.post.application.PostService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
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
        validateOwner(ownerId, blogSlug)

        val totalPosts = postService.countPublishedPosts(blogInfo.blogId)
        val visitStats = analyticsService.getVisitStats(blogInfo.blogId)

        return DashboardStatsInfo(
            todayVisitors = visitStats.todayVisitors,
            weekVisitors = visitStats.weekVisitors,
            totalPosts = totalPosts,
        )
    }

    fun getPopularPosts(ownerId: UUID, blogSlug: String): List<PopularPostInfo> {
        val blogInfo = blogService.getBlogInfo(blogSlug)
        validateOwner(ownerId, blogSlug)

        val today = LocalDate.now()
        val startDate = today.minusDays(29)

        val popularPosts = analyticsService.getPopularPosts(
            blogId = blogInfo.blogId,
            startDate = startDate,
            endDate = today,
            limit = 5
        )

        return popularPosts.map {
            PopularPostInfo(
                id = it.postId,
                title = it.title,
                views = it.viewCount
            )
        }
    }

    fun getTrafficData(ownerId: UUID, blogSlug: String, timeRange: TimeRange): List<TrafficChartInfo> {
        val blogInfo = blogService.getBlogInfo(blogSlug)
        validateOwner(ownerId, blogSlug)

        val today = LocalDate.now()

        val (startDate, formatter) = when (timeRange) {
            TimeRange.TWO_WEEKS -> today.minusDays(13) to DateTimeFormatter.ofPattern("MM/dd")
            TimeRange.THIRTY_DAYS -> today.minusDays(29) to DateTimeFormatter.ofPattern("MM/dd")
            TimeRange.NINETY_DAYS -> today.minusMonths(3) to DateTimeFormatter.ofPattern("yyyy/MM")
        }

        val trafficData = analyticsService.getTrafficData(
            blogId = blogInfo.blogId,
            startDate = startDate,
            endDate = today,
            formatter = formatter
        )

        return trafficData.map {
            TrafficChartInfo(
                date = it.date,
                visitors = it.visitors
            )
        }
    }

    private fun validateOwner(ownerId: UUID, blogSlug: String) {
        val blogInfo = blogService.getBlogInfo(blogSlug)
        if (blogInfo.userId != ownerId) {
            throw ContentriaException(ErrorCode.FORBIDDEN_ACCESS_BLOG)
        }
    }
}