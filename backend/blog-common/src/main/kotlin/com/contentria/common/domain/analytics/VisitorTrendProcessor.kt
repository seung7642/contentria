package com.contentria.common.domain.analytics

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class VisitorTrendProcessor {

    fun generateTrendSeries(
        startDate: LocalDate,
        endDate: LocalDate,
        historyStatsMap: Map<LocalDate, DailyStatistics>,
        todayVisitors: Long
    ): Map<LocalDate, Long> {
        val result = mutableMapOf<LocalDate, Long>()
        val today = LocalDate.now()

        var currentDate = startDate
        while (!currentDate.isAfter(endDate)) {
            val visitors = if (currentDate.isEqual(today)) {
                todayVisitors
            } else {
                historyStatsMap[currentDate]?.visitCount ?: 0L
            }

            result[currentDate] = visitors
            currentDate = currentDate.plusDays(1)
        }

        return result
    }
}