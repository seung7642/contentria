package com.contentria.api.auth.infrastructure

import com.contentria.api.auth.application.CaptchaProvider
import com.contentria.api.auth.application.dto.CaptchaCommand
import com.contentria.api.auth.application.dto.CaptchaVersion
import com.contentria.api.auth.infrastructure.dto.GoogleRecaptchaResponse
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import com.contentria.api.global.properties.AppProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

private val log = KotlinLogging.logger {}

@Component
class GoogleRecaptchaProvider(
    private val webClient: WebClient,
    appProperties: AppProperties
) : CaptchaProvider {

    private val recaptchaProperties = appProperties.auth.recaptcha

    override fun verify(command: CaptchaCommand) {
        val secretKey = when (command.version) {
            CaptchaVersion.V2 -> recaptchaProperties.v2SecretKey
            CaptchaVersion.V3 -> recaptchaProperties.v3SecretKey
        }

        if (secretKey.isBlank()) {
            log.error { "reCAPTCHA ${command.version} secret key is missing." }
            throw ContentriaException(ErrorCode.INTERNAL_SERVER_ERROR)
        }

        val response = callGoogleApi(secretKey, command.token, command.clientIp)

        if (!response.success) {
            log.warn { "reCAPTCHA verification failed. ErrorCodes: ${response.errorCodes}" }
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }

        if (command.version.isV3()) {
            validateV3Specifics(response, command.action, recaptchaProperties.scoreThreshold)
        }
    }

    private fun validateV3Specifics(
        response: GoogleRecaptchaResponse,
        expectedAction: String?,
        threshold: Double
    ) {
        if (expectedAction != null && response.action != expectedAction) {
            log.warn { "reCAPTCHA action mismatch. Expected: $expectedAction, Got: ${response.action}" }
            throw ContentriaException(ErrorCode.RECAPTCHA_V3_ACTION_MISMATCH)
        }

        val score = response.score ?: 0.0
        if (score < threshold) {
            log.info { "reCAPTCHA score too low. Score: $score, Threshold: $threshold" }
            throw ContentriaException(ErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }
    }

    private fun callGoogleApi(secret: String, token: String, ip: String?): GoogleRecaptchaResponse {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("secret", secret)
            add("response", token)
            if (ip != null) {
                add("remoteip", ip)
            }
        }

        return webClient.post()
            .uri(recaptchaProperties.siteVerifyUrl)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(GoogleRecaptchaResponse::class.java)
            .block()
            ?: throw ContentriaException(ErrorCode.RECAPTCHA_API_ERROR)
    }
}