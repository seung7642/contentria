package com.contentria.api.auth.infrastructure.dto

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap

data class GoogleRecaptchaRequest(
    val secret: String,
    val response: String,
    val remoteip: String? = null
) {
    fun toFormData(): MultiValueMap<String, String> {
        return LinkedMultiValueMap<String, String>().apply {
            add("secret", secret)
            add("response", response)
            remoteip?.let { add("remoteip", it) }
        }
    }
}