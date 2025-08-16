package com.contentria.api.auth.service

import com.contentria.api.auth.dto.RecaptchaVerificationResult
import reactor.core.publisher.Mono

interface RecaptchaService {

    fun verifyRecaptchaV3(token: String?, clientIp: String?, expectedAction: String?): Mono<RecaptchaVerificationResult>

    fun verifyRecaptchaV2(token: String?, clientIp: String?): Mono<RecaptchaVerificationResult>
}
