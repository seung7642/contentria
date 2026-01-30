package com.contentria.api.dashboard.controller.dto

import com.contentria.api.dashboard.application.dto.TrafficChartInfo

data class TrafficChartResponse(
    val date: String,
    val visitors: Long
) {
    companion object {
        fun from(trafficChartInfo: TrafficChartInfo): TrafficChartResponse {
            return TrafficChartResponse(
                date = trafficChartInfo.date,
                visitors = trafficChartInfo.visitors
            )
        }
    }
}
