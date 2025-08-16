package com.demo.com.contentria.api.auth.service

interface VerificationCodeService {

    fun generateAndSend(email: String, userName: String): String

    fun verify(email: String, code: String): Boolean

    fun hasRecentValidCode(email: String): Boolean
}