package com.contentria.api.analytics.application.dto

data class VisitStatsInfo(
    val todayVisitors: Long,
    val todayViews: Long,

    val todayVisitorsGrowthRate: Double?,
    val todayViewsGrowthRate: Double?,

    val yesterdayVisitors: Long,
    val yesterdayViews: Long,

    val totalViews: Long
)
