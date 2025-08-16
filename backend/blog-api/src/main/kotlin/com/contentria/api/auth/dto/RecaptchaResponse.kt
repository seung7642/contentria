package com.contentria.api.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class RecaptchaResponse(
    val success: Boolean,
    val score: Double?, // v3의 경우 점수
    val action: String?, // v3의 경우 프론트엔드에서 설정한 액션 이름
    @param:JsonProperty("challenge_ts")
    val challengeTs: Instant?, // ISO 8601 형식의 타임스탬프
    val hostname: String?,
    @param:JsonProperty("error-codes")
    val errorCodes: List<String>?
)
