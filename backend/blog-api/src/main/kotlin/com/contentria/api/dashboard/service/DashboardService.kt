package com.contentria.api.dashboard.service

import com.contentria.api.dashboard.dto.DashboardStatsResponse
import com.contentria.api.dashboard.dto.PopularPostResponse
import com.contentria.api.dashboard.dto.TimeRange
import com.contentria.api.dashboard.dto.TrafficChartDataResponse
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Service
class DashboardService {

    fun getStats(slug: String): DashboardStatsResponse {
        // Dummy data for demonstration purposes
        return DashboardStatsResponse(
            todayVisitors = 0,
            weekVisitors = 0,
            weekNewComments = 0,
            totalSubscribers = 0
        )
    }

    fun getPopularPosts(slug: String): List<PopularPostResponse> {
        return listOf(
            PopularPostResponse(id = "1", title = "Understanding Kotlin Coroutines", views = 0),
            PopularPostResponse(id = "2", title = "Spring Boot with Kotlin: A Comprehensive Guide", views = 0),
            PopularPostResponse(id = "3", title = "Building RESTful APIs with Ktor", views = 0)
        )
    }

    fun getTrafficData(slug: String, timeRange: TimeRange): List<TrafficChartDataResponse> {
        val today = LocalDate.now()

        return when (timeRange) {
            TimeRange.TWO_WEEKS -> (0..13).map { i ->
                val date = today.minusDays(i.toLong())
                TrafficChartDataResponse(
                    date = date.format(DateTimeFormatter.ofPattern("MM/dd")),
                    visitors = 0
                )
            }.reversed()

            TimeRange.THIRTY_DAYS -> (0..29).map { i ->
                val date = today.minusDays(i.toLong())
                TrafficChartDataResponse(
                    date = date.format(DateTimeFormatter.ofPattern("MM/dd")),
                    visitors = 0 // 20 ~ 120
                )
            }.reversed()

            TimeRange.NINETY_DAYS -> (0..11).map { i ->
                val month = today.minusMonths(i.toLong())
                TrafficChartDataResponse(
                    date = month.format(DateTimeFormatter.ofPattern("yyyy/MM")),
                    visitors = 0 // 50 ~ 250
                )
            }.reversed()
        }
    }
}