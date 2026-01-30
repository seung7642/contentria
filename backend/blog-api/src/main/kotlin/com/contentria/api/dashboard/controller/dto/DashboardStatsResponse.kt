package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.DashboardStatsInfo

data class DashboardStatsResponse(
    val todayVisitors: Long,
    val todayGrowthRate: Double?,
    val weekVisitors: Long,
    val weekGrowthRate: Double?,
    val totalPosts: Long
) {
    companion object {
        fun from(dashboardStatsInfo: DashboardStatsInfo): DashboardStatsResponse {
            return DashboardStatsResponse(
                todayVisitors = dashboardStatsInfo.todayVisitors,
                todayGrowthRate = dashboardStatsInfo.todayGrowthRate,
                weekVisitors = dashboardStatsInfo.weekVisitors,
                weekGrowthRate = dashboardStatsInfo.weekGrowthRate,
                totalPosts = dashboardStatsInfo.totalPosts
            )
        }
    }
}
