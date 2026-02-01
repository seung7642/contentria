package com.contentria.api.analytics.domain

import org.springframework.stereotype.Component
import kotlin.math.round

@Component
class StatisticsCalculator {

    fun calculateTotalViews(historyTotal: Long?, todayViews: Long): Long {
        return (historyTotal ?: 0L) + todayViews
    }

    fun calculateGrowthRate(current: Long, previous: Long): Double? {
        if (previous == 0L) {
            return null
        }

        val rate = ((current - previous).toDouble() / previous) * 100
        return round(rate * 10) / 10.0
    }
}