package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.DashboardStatsInfo

data class DashboardStatsResponse(
    val todayVisitors: Long,
    val yesterdayVisitors: Long,
    val totalVisitors: Long,
    val todayViews: Long,
    val yesterdayViews: Long,
    val totalViews: Long,
    val totalPosts: Long
) {
    companion object {
        fun from(dashboardStatsInfo: DashboardStatsInfo): DashboardStatsResponse {
            return DashboardStatsResponse(
                todayVisitors = dashboardStatsInfo.todayVisitors,
                yesterdayVisitors = dashboardStatsInfo.yesterdayVisitors,
                totalVisitors = dashboardStatsInfo.totalVisitors,
                todayViews = dashboardStatsInfo.todayViews,
                yesterdayViews = dashboardStatsInfo.yesterdayViews,
                totalViews = dashboardStatsInfo.totalViews,
                totalPosts = dashboardStatsInfo.totalPosts
            )
        }
    }
}
