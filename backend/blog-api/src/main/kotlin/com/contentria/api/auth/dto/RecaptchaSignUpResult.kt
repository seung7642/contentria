package com.contentria.api.auth.dto

enum class RecaptchaSignUpResult {
    PROCEED, // reCAPTCHA 검증 성공, 회원가입 진행 가능
    REQUIRE_V2_CHALLENGE, // reCAPTCHA v3 점수가 낮아 추가로 v2 인증 필요
}