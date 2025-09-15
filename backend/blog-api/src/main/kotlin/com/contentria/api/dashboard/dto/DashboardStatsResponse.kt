package com.contentria.api.dashboard.dto

data class DashboardStatsResponse(
    val todayVisitors: Int,
    val weekVisitors: Int,
    val weekNewComments: Int,
    val totalSubscribers: Int
)
