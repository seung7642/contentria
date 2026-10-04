package com.contentria.common.domain.analytics

import org.springframework.stereotype.Component

@Component
class StatisticsCalculator {

    fun calculateTotalViews(historyTotal: Long?, todayViews: Long): Long {
        return (historyTotal ?: 0L) + todayViews
    }
}