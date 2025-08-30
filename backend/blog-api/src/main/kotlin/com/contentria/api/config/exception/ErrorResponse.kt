package com.contentria.api.config.exception

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

data class ErrorResponse(
    val timestamp: ZonedDateTime = ZonedDateTime.now(),
    val status: Int,
    val error: String,
    val code: String,
    val message: String?,
    val path: String? = null,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val details: Map<String, String> = emptyMap()
)