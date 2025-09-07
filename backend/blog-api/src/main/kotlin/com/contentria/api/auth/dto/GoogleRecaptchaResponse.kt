package com.contentria.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class GoogleRecaptchaResponse(
    val success: Boolean,
    val hostname: String?,

    @JsonProperty("challenge_ts")
    val challengeTimestamp: ZonedDateTime?,

    @JsonProperty("error-codes")
    val errorCodes: List<String>?,

    val action: String?,
    val score: Double?
) {
    fun isValidAction(expectedAction: String): Boolean {
        return action == expectedAction
    }

    fun isHighScore(threshold: Double): Boolean {
        return score != null && score >= threshold
    }
}