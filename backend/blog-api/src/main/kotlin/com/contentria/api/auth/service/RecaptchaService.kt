package com.contentria.api.auth.service

import com.contentria.api.auth.dto.RecaptchaResponse
import com.contentria.api.auth.dto.RecaptchaVerificationResult
import com.contentria.api.auth.dto.RecaptchaVerifyRequest
import com.contentria.api.config.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Service
class RecaptchaService(
    private val webClient: WebClient,
    appProperties: AppProperties
) {

    private val recaptchaProperties = appProperties.auth.recaptcha

    fun verifyRecaptchaV3(
        token: String?,
        clientIp: String?,
        action: String?
    ): Mono<RecaptchaVerificationResult> {
        if (token.isNullOrBlank()) {
            logger.warn { "reCAPTCHA v3 token is null or blank." }
            return Mono.just(
                RecaptchaVerificationResult(
                    success = false, score = null, action = null, hostname = null,
                    errorCodes = listOf("missing-input-response"), rawGoogleSuccess = false,
                    details = "reCAPTCHA V3 Token was null or blank."
                )
            )
        }

        val request = RecaptchaVerifyRequest(
            secret = recaptchaProperties.v3SecretKey,
            response = token,
            remoteip = clientIp
        )

        return callGoogleSiteVerifyApi(request) { response ->
            logger.info { "Google reCAPTCHA v3 raw response: $response" }
            var overallSuccess = response.success

            val expectedAction = action ?: recaptchaProperties.expectedActions[response.action]
            if (overallSuccess && !expectedAction.isNullOrEmpty() && expectedAction != response.action) {
                logger.warn { "reCAPTCHA action mismatch. Expected: ${expectedAction}, Actual: ${response.action}" }
                overallSuccess = false
            }

            RecaptchaVerificationResult(
                success = overallSuccess,
                score = response.score,
                action = response.action,
                hostname = response.hostname,
                errorCodes = response.errorCodes,
                rawGoogleSuccess = response.success,
                details = if (!overallSuccess) "Verification checks failed. Google Raw Response: $response" else "Verification successful."
            )
        }
    }

    fun verifyRecaptchaV2(token: String?, clientIp: String?): Mono<RecaptchaVerificationResult> {
        if (token.isNullOrBlank()) {
            logger.warn { "reCAPTCHA v2 token is null or blank." }
            return Mono.just(
                RecaptchaVerificationResult(
                    success = false, score = null, action = null, hostname = null,
                    errorCodes = listOf("missing-input-response"), rawGoogleSuccess = false,
                    details = "reCAPTCHA V2 Token was null or blank."
                )
            )
        }

        if (recaptchaProperties.v2SecretKey.isBlank()) {
            logger.warn { "reCAPTCHA v2 secret key is not configured." }
            return Mono.just(
                RecaptchaVerificationResult(
                    success = false, score = null, action = null, hostname = null,
                    errorCodes = listOf("recaptcha-v2-secret-key-missing"), rawGoogleSuccess = false,
                    details = "reCAPTCHA V2 Secret Key is not configured."
                )
            )
        }

        val request = RecaptchaVerifyRequest(
            secret = recaptchaProperties.v2SecretKey,
            response = token,
            remoteip = clientIp
        )

        return callGoogleSiteVerifyApi(request) { response ->
            var overallSuccess = response.success

            if (overallSuccess && !recaptchaProperties.expectedHostname.isNullOrBlank() && recaptchaProperties.expectedHostname != response.hostname) {
                logger.warn { "reCAPTCHA v2 hostname mismatch. Expected ${recaptchaProperties.expectedHostname}, Actual: ${response.hostname}" }
                overallSuccess = false
            }
            RecaptchaVerificationResult(
                success = overallSuccess,
                score = null, // reCAPTCHA v2 does not provide a score
                action = null, // reCAPTCHA v2 does not have an action
                hostname = response.hostname,
                errorCodes = response.errorCodes,
                rawGoogleSuccess = response.success,
                details = if (!response.success) "Verification failed. Google Raw Response: $response" else "Verification successful."
            )
        }
    }

    private fun callGoogleSiteVerifyApi(
        request: RecaptchaVerifyRequest,
        responseMapper: (RecaptchaResponse) -> RecaptchaVerificationResult
    ): Mono<RecaptchaVerificationResult> {
        return webClient.post()
            .uri(recaptchaProperties.siteVerifyUrl)
            .body(BodyInserters.fromFormData(request.toFormData()))
            .retrieve()
            .bodyToMono(RecaptchaResponse::class.java)
            .map(responseMapper)
            .onErrorResume { e ->
                logger.error(e) { "Error during reCAPTCHA verification HTTP request: $e.message" }
                Mono.just(
                    RecaptchaVerificationResult(
                        success = false, score = null, action = null, hostname = null,
                        errorCodes = listOf("recaptcha-request-failed"), rawGoogleSuccess = false,
                        details = "HTTP request to Google reCAPTCHA API failed: ${e.message}"
                    )
                )
            }
    }
}