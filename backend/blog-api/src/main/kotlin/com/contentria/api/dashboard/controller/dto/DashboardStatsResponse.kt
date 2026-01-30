package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.DashboardStatsInfo

data class DashboardStatsResponse(
    val todayVisitors: Long,
    val weekVisitors: Long,
    val totalPosts: Long
) {
    companion object {
        fun from(dashboardStatsInfo: DashboardStatsInfo): DashboardStatsResponse {
            return DashboardStatsResponse(
                todayVisitors = dashboardStatsInfo.todayVisitors,
                weekVisitors = dashboardStatsInfo.weekVisitors,
                totalPosts = dashboardStatsInfo.totalPosts
            )
        }
    }
}
