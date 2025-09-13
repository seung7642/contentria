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

    fun isV2TokenValid(token: String, clientIp: String?): Boolean {
        val response = verifyV2(token, clientIp)
        return response.success
    }

    fun isV3TokenValid(token: String, clientIp: String?, expectedAction: String, scoreThreshold: Double): Boolean {
        val response = verifyV3(token, clientIp)

        if (!response.success) {
            log.warn { "reCAPTCHA V3 verification failed. Google Errors: ${response.errorCodes}" }
            return false
        }
        if (response.action != expectedAction) {
            log.warn { "reCAPTCHA action mismatch. Expected: $expectedAction, Got: ${response.action}" }
            return false
        }
        if ((response.score ?: 0.0) < scoreThreshold) {
            log.info { "reCAPTCHA score is below threshold. Score: ${response.score}, Threshold: $scoreThreshold" }
            return false
        }
        return true
    }

    private fun verifyV3(token: String, clientIp: String?): GoogleRecaptchaResponse {
        if (recaptchaProperties.v3SecretKey.isBlank()) {
            log.warn { "reCAPTCHA v3 secret key is not configured. Verification will be skipped." }
            return createErrorResponse("missing-v3-secret-key")
        }

        val requestBody = GoogleRecaptchaRequest(
            secret = recaptchaProperties.v3SecretKey,
            response = token,
            remoteip = clientIp
        )

        return callGoogleApi(requestBody).block()!!
    }

    private fun verifyV2(token: String, clientIp: String?): GoogleRecaptchaResponse {
        if (recaptchaProperties.v2SecretKey.isBlank()) {
            log.warn { "reCAPTCHA v2 secret key is not configured. Verification will be skipped." }
            return createErrorResponse("missing-v2-secret-key")
        }

        val requestBody = GoogleRecaptchaRequest(
            secret = recaptchaProperties.v2SecretKey,
            response = token,
            remoteip = clientIp
        )

        return callGoogleApi(requestBody).block()!!
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