package com.contentria.api.auth.dto

/**
 * '회원가입' 비즈니스 로직에서의 reCAPTCHA 검증 결과를 나타내는 Sealed Class 입니다.
 * 결과는 '성공', 'V2 인증 필요', '실패' 중 하나일 수 있으며, 다른 상태는 존재할 수 없습니다.
 * 이를 통해 호출부(SignUpService)에서 when 구문을 사용하여 모든 케이스를 강제적으로 처리하게 만들어 코드의 안정성을 높입니다.
 */
sealed class RecaptchaSignUpResult {
    data object Success : RecaptchaSignUpResult()

    data object V2Required : RecaptchaSignUpResult()

    data class Failure(val reason: String, val googleErrorCodes: List<String>? = null) : RecaptchaSignUpResult()
}