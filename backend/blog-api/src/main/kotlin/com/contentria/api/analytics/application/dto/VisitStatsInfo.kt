package com.contentria.api.analytics.application.dto

data class VisitStatsInfo(
    val todayViews: Long,
    val yesterdayViews: Long,
    val todayVisitors: Long,
    val yesterdayVisitors: Long,
    val weekVisitors: Long,
    val prevWeekVisitors: Long
)
