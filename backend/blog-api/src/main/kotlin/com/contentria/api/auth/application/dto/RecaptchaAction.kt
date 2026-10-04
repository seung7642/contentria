package com.contentria.api.auth.application.dto

enum class RecaptchaAction(val value: String) {

    SIGN_UP("signup_initiate"),
    LOGIN("login_with_password"),
    SEND_OTP("send_otp_code")
}