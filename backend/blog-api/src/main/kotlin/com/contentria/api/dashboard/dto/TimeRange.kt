package com.contentria.api.dashboard.dto

import com.fasterxml.jackson.annotation.JsonValue

enum class TimeRange(@get:JsonValue val value: String) {

    TWO_WEEKS("2weeks"),
    THIRTY_DAYS("30days"),
    NINETY_DAYS("90days"),
}