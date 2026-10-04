package com.contentria.api.auth.application.dto

enum class CaptchaVersion {

    V2, V3;

    fun isV2(): Boolean = this == V2
    fun isV3(): Boolean = this == V3
}