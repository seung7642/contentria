package com.contentria.api.global.converter

import com.contentria.api.dashboard.dto.TimeRange
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component

@Component
class StringToTimeRangeConverter : Converter<String, TimeRange> {

    override fun convert(source: String): TimeRange? {
        return TimeRange.entries.find { it.value.equals(source, ignoreCase = true) }
    }
}