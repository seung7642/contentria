package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.CaptchaVerifier
import com.contentria.api.auth.application.dto.CaptchaCommand
import com.contentria.api.auth.application.dto.CaptchaVersion
import com.contentria.api.auth.controller.dto.RecaptchaRequest
import com.contentria.api.auth.infrastructure.dto.GoogleRecaptchaRequest
import com.contentria.api.auth.infrastructure.dto.GoogleRecaptchaResponse
import com.contentria.api.global.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class GoogleRecaptchaAdapter(
    private val webClient: WebClient,
    appProperties: AppProperties
) : CaptchaVerifier {

    private val recaptchaProperties = appProperties.auth.recaptcha

    override fun verify(command: CaptchaCommand): Boolean {
        return when (command.version) {
            CaptchaVersion.V2 -> isV2TokenValid(command.token, command.clientIp)
            CaptchaVersion.V3 -> isV3TokenValid(
                token = command.token,
                clientIp = command.clientIp,
                expectedAction = command.action,
                scoreThreshold = recaptchaProperties.scoreThreshold
            )
        }
    }

    fun isValid(request: RecaptchaRequest, action: String, clientIp: String?): Boolean {
        return if (request.hasRecaptchaV2Token()) {
            isV2TokenValid(request.recaptchaV2Token!!, clientIp)
        } else if (request.hasRecaptchaV3Token()) {
            isV3TokenValid(
                request.recaptchaV3Token!!,
                clientIp,
                action,
                recaptchaProperties.scoreThreshold
            )
        } else {
            false
        }
    }

    private fun isV2TokenValid(token: String, clientIp: String?): Boolean {
        val response = verifyV2(token, clientIp)
        return response.success
    }

    private fun isV3TokenValid(token: String, clientIp: String?, expectedAction: String, scoreThreshold: Double): Boolean {
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