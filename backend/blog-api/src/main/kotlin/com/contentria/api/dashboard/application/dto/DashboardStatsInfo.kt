package com.contentria.api.dashboard.application.dto

data class DashboardStatsInfo(
    val todayVisitors: Long,
    val todayVisitorsGrowthRate: Double?,
    val todayViews: Long,
    val todayViewsGrowthRate: Double?,
    val totalViews: Long,
    val totalPosts: Long
)
