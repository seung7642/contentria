package com.demo.blog.auth.service

import com.demo.blog.auth.dto.AuthResponse
import com.demo.blog.auth.dto.GoogleTokenResponse
import com.demo.blog.auth.dto.GoogleUserInfo
import com.demo.blog.auth.dto.GoogleUserInfoResponse
import com.demo.blog.common.config.GoogleProperties
import com.demo.blog.common.security.JwtService
import com.demo.blog.user.service.CustomerUserDetailsService
import com.demo.blog.user.service.UserService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class GoogleAuthService(
    private val userService: UserService,
    private val customerUserDetailsService: CustomerUserDetailsService,
    private val jwtService: JwtService,
    private val restTemplate: RestTemplate,
    private val googleProperties: GoogleProperties
) {
    fun authenticateWithCode(code: String): AuthResponse {
        // 1. 인증 코드를 Google API 토큰으로 교환
        val tokenResponse = try {
            exchangeCodeForTokens(code)
        } catch (e: Exception) {
            println("Google API Error: ${e.message}")
            throw e
        }

        // 2. Google API 토큰으로 사용자 정보 가져오기
        val userInfo = getUserInfoFromGoogle(tokenResponse.access_token)

        // 3. 사용자 정보 저장 또는 업데이트
        val user = userService.createOrUpdateGoogleUser(userInfo)

        // 4. JWT 토큰 생성
        val userDetails = customerUserDetailsService.loadUserByUsername(userInfo.email)
        val jwtToken = jwtService.generateToken(userDetails)

        return AuthResponse(
            token = jwtToken,
            userId = user.id,
            email = user.email,
            name = user.username,
            profileImage = user.pictureUrl
        )
    }

    // 인증 코드를 토큰으로 교환
    private fun exchangeCodeForTokens(code: String): GoogleTokenResponse {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val params = LinkedMultiValueMap<String, String>().apply {
            add("code", code)
            add("client_id", googleProperties.clientId)
            add("client_secret", googleProperties.clientSecret)
            add("redirect_uri", googleProperties.redirectUri)
            add("grant_type", "authorization_code")
        }

        val request = HttpEntity<MultiValueMap<String, String>>(params, headers)
        val response = restTemplate.exchange(
            "https://oauth2.googleapis.com/token",
            HttpMethod.POST,
            request,
            GoogleTokenResponse::class.java
        )

        return response.body ?: throw RuntimeException("Failed to retrieve token from Google")
    }

    // 액세스 토큰으로 사용자 정보 가져오기
    private fun getUserInfoFromGoogle(accessToken: String): GoogleUserInfo {
        val headers = HttpHeaders().apply {
            setBearerAuth(accessToken)
        }

        val request = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            "https://www.googleapis.com/oauth2/v3/userinfo",
            HttpMethod.GET,
            request,
            GoogleUserInfoResponse::class.java
        )

        val userInfoResponse = response.body ?: throw RuntimeException("Failed to retrieve user info from Google")

        return GoogleUserInfo(
            id = userInfoResponse.sub,
            email = userInfoResponse.email,
            name = userInfoResponse.name,
            picture = userInfoResponse.picture
        )
    }
}