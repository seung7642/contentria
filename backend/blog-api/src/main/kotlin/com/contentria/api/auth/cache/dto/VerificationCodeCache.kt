package com.contentria.api.auth.cache.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.ZonedDateTime

data class VerificationCodeCache(
    @param:JsonProperty("code")
    val code: String,

    @param:JsonProperty("email")
    val email: String,

    @param:JsonProperty("name")
    val name: String,

    @param:JsonProperty("attempts")
    val attempts: Int = 0,

    @param:JsonProperty("createdAt")
    val createdAt: ZonedDateTime,

    @param:JsonProperty("expiresAt")
    val expiresAt: ZonedDateTime
)
