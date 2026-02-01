package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.DashboardStatsInfo

data class DashboardStatsResponse(
    val todayVisitors: Long,
    val todayVisitorsGrowthRate: Double?,
    val todayViews: Long,
    val todayViewsGrowthRate: Double?,
    val totalViews: Long,
    val totalPosts: Long
) {
    companion object {
        fun from(dashboardStatsInfo: DashboardStatsInfo): DashboardStatsResponse {
            return DashboardStatsResponse(
                todayVisitors = dashboardStatsInfo.todayVisitors,
                todayVisitorsGrowthRate = dashboardStatsInfo.todayVisitorsGrowthRate,
                todayViews = dashboardStatsInfo.todayViews,
                todayViewsGrowthRate = dashboardStatsInfo.todayViewsGrowthRate,
                totalViews = dashboardStatsInfo.totalViews,
                totalPosts = dashboardStatsInfo.totalPosts
            )
        }
    }
}
