package com.demo.blog.auth.service

interface VerificationCodeService {

    fun generateAndSend(email: String, userName: String): String

    fun verify(email: String, code: String): Boolean

    fun hasRecentValidCode(email: String): Boolean
}