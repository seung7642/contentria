package com.contentria.api.auth.application

interface VerificationCodeProvider {

    fun sendVerificationCode(email: String, name: String?)

    fun verifyCode(email: String, code: String)
}