package com.contentria.api.auth.dto

data class RecaptchaVerificationResult(
    val success: Boolean, // Google API 자체의 success + 추가 검증(hostname, action) 결과
    val score: Double?,
    val action: String?,
    val hostname: String?,
    val errorCodes: List<String>?,
    val rawGoogleSuccess: Boolean, // Google API 응답의 'success' 필드 값
    val details: String? = null
) {
    fun isScoreSufficient(threshold: Double): Boolean = score != null && score >= threshold
}
