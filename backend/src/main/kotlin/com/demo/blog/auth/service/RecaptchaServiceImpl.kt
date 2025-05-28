package com.demo.blog.auth.service

import com.demo.blog.common.properties.AppProperties
import org.springframework.stereotype.Service

@Service
class RecaptchaServiceImpl(
//    private val restTemplate: RestTemplate,
    private val appProperties: AppProperties
) : RecaptchaService {

    companion object {
        private const val RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify"
    }

    override fun isValidRecaptchaV3(token: String?, clientIp: String?): Boolean {
        if (token.isNullOrBlank()) {
            return false
        }

        // TODO: Implement Google reCAPTCHA v3 verification logic
//        val secretKey = appProperties.recaptcha.v3SecretKey
//        val params = LinkedMultiValueMap<String, String>()
//        params.add("secret", secretKey)
//        params.add("response", token)
//        clientIp?.let { params.add("remoteip", it) }
//        val response = restTemplate.postForObject(RECAPTCHA_VERIFY_URL, params, Map::class.java)
//        return response?.get("success") == true && (response["score"] as? Double ?: 0.0) >= 0.5
        return true
    }

    override fun isValidRecaptchaV2(token: String?, clientIp: String?): Boolean {
        if (token.isNullOrBlank()) {
            return false
        }

        // TODO: Implement Google reCAPTCHA v2 verification logic
//        val secretKey = appProperties.recaptcha.v2SecretKey
        return true
    }
}