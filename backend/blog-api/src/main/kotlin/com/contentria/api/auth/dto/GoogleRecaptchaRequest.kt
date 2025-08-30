package com.contentria.api.auth.dto

data class GoogleRecaptchaRequest(
    val secret: String,
    val response: String,
    val remoteip: String? = null
)