package com.contentria.api.analytics.application.dto

data class VisitStatsInfo(
    val todayVisitors: Long,
    val yesterdayVisitors: Long,
    val totalVisitors: Long,

    val todayViews: Long,
    val yesterdayViews: Long,
    val totalViews: Long
)
