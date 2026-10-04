package com.contentria.api.dashboard.application.dto

data class DashboardStatsInfo(
    val todayVisitors: Long,
    val yesterdayVisitors: Long,
    val totalVisitors: Long,
    val todayViews: Long,
    val yesterdayViews: Long,
    val totalViews: Long,
    val totalPosts: Long
)
