package com.contentria.api.dashboard.controller.dto

data class DashboardStatsResponse(
    val todayVisitors: Int,
    val weekVisitors: Int,
    val weekNewComments: Int,
    val totalSubscribers: Int
)
