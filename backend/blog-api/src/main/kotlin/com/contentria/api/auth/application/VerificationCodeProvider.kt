package com.contentria.api.auth.application

interface VerificationCodeProvider {

    fun sendVerificationCode(email: String, name: String? = null): String

    fun verifyCode(email: String, code: String): Boolean
}