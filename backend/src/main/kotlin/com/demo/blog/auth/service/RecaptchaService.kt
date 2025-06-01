package com.demo.blog.auth.service

import com.demo.blog.auth.dto.RecaptchaVerificationResult
import reactor.core.publisher.Mono

interface RecaptchaService {

    fun verifyRecaptchaV3(token: String?, clientIp: String?, expectedAction: String?): Mono<RecaptchaVerificationResult>

    fun verifyRecaptchaV2(token: String?, clientIp: String?): Mono<RecaptchaVerificationResult>
}
