package com.demo.blog.auth.service

interface RecaptchaService {

    fun isValidRecaptchaV3(token: String?, clientIp: String?): Boolean

    fun isValidRecaptchaV2(token: String?, clientIp: String?): Boolean
}
