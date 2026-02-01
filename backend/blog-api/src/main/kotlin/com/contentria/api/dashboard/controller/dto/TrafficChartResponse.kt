package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.VisitorTrendChartInfo

data class TrafficChartResponse(
    val date: String,
    val visitors: Long
) {
    companion object {
        fun from(visitorTrendChartInfo: VisitorTrendChartInfo): TrafficChartResponse {
            return TrafficChartResponse(
                date = visitorTrendChartInfo.date,
                visitors = visitorTrendChartInfo.visitors
            )
        }
    }
}
