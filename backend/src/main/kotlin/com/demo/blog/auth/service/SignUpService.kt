package com.demo.blog.auth.service

import com.demo.blog.auth.dto.SignUpInitiateRequest
import com.demo.blog.auth.dto.SignUpInitiateResponse
import com.demo.blog.auth.dto.SignUpResponse
import com.demo.blog.auth.dto.VerificationCodeRequest
import jakarta.servlet.http.HttpServletRequest

interface SignUpService {

    fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest): SignUpInitiateResponse

    fun verifyCode(request: VerificationCodeRequest): SignUpResponse

    fun resendVerificationCode(email: String): Boolean
}