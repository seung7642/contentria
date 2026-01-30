package com.contentria.api.dashboard.application.dto

data class DashboardStatsInfo(
    val todayViews: Long,
    val todayViewsGrowthRate: Double?,
    val todayVisitors: Long,
    val todayGrowthRate: Double?,
    val weekVisitors: Long,
    val weekGrowthRate: Double?,
    val totalPosts: Long
)
