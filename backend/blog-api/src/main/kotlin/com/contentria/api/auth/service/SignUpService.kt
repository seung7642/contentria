package com.contentria.api.auth.service

import com.contentria.api.auth.dto.SignUpInitiateRequest
import com.contentria.api.auth.dto.SignUpInitiateResponse
import com.contentria.api.auth.dto.SignUpResponse
import com.contentria.api.auth.dto.VerificationCodeRequest
import jakarta.servlet.http.HttpServletRequest

interface SignUpService {

    fun initiate(request: SignUpInitiateRequest, httpRequest: HttpServletRequest): SignUpInitiateResponse

    fun verifyCode(request: VerificationCodeRequest): SignUpResponse

    fun resendVerificationCode(email: String): Boolean
}