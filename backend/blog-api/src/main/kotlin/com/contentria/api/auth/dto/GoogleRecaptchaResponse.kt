package com.contentria.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class GoogleRecaptchaResponse(
    val success: Boolean,
    val score: Double?,
    val action: String?,
    val hostname: String?,

    @JsonProperty("error-codes")
    val errorCodes: List<String>?,

    @JsonProperty("challenge_ts")
    val challengeTimestamp: ZonedDateTime?
)