package com.contentria.api.auth.service

import com.contentria.api.auth.dto.GoogleRecaptchaRequest
import com.contentria.api.auth.dto.GoogleRecaptchaResponse
import com.contentria.api.config.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Service
class RecaptchaService(
    private val webClient: WebClient,
    appProperties: AppProperties
) {

    private val recaptchaProperties = appProperties.auth.recaptcha

    fun verifyV3(token: String, clientIp: String?): Mono<GoogleRecaptchaResponse> {
        if (recaptchaProperties.v3SecretKey.isBlank()) {
            log.warn { "reCAPTCHA v3 secret key is not configured. Verification will be skipped." }
            return Mono.just(createErrorResponse("missing-v3-secret-key"))
        }

        val requestBody = GoogleRecaptchaRequest(
            secret = recaptchaProperties.v3SecretKey,
            response = token,
            remoteip = clientIp
        )

        return callGoogleApi(requestBody)
    }

    fun verifyV2(token: String, clientIp: String?): Mono<GoogleRecaptchaResponse> {
        if (recaptchaProperties.v2SecretKey.isBlank()) {
            log.warn { "reCAPTCHA v2 secret key is not configured. Verification will be skipped." }
            return Mono.just(createErrorResponse("missing-v2-secret-key"))
        }

        val requestBody = GoogleRecaptchaRequest(
            secret = recaptchaProperties.v2SecretKey,
            response = token,
            remoteip = clientIp
        )

        return callGoogleApi(requestBody)
    }

    private fun callGoogleApi(requestBody: GoogleRecaptchaRequest): Mono<GoogleRecaptchaResponse> {
        return webClient.post()
            .uri(recaptchaProperties.siteVerifyUrl)
            .body(BodyInserters.fromFormData(requestBody.toFormData()))
            .retrieve()
            .bodyToMono(GoogleRecaptchaResponse::class.java)
            .doOnError { e -> log.error(e) { "reCAPTCHA API call failed" } }
    }

    private fun createErrorResponse(errorCode: String): GoogleRecaptchaResponse {
        return GoogleRecaptchaResponse(
            success = false,
            score = null,
            action = null,
            hostname = null,
            errorCodes = listOf(errorCode),
            challengeTimestamp = null
        )
    }
}