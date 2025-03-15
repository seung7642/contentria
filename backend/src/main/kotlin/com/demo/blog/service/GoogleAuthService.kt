package com.demo.blog.service

import com.demo.blog.dto.AuthResponse
import com.demo.blog.dto.GoogleTokenRequest
import com.demo.blog.dto.GoogleUserInfo
import com.demo.blog.security.JwtService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class GoogleAuthService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val webClient: WebClient.Builder
) {

    private val googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo"

    fun authenticateGoogleUser(tokenRequest: GoogleTokenRequest): AuthResponse {
        // Google ID 토큰을 사용하여 사용자 정보 가져오기
        val userInfo = getUserInfoFromGoogle(tokenRequest.credential)

        // 사용자 정보 저장 또는 업데이트
        val user = userService.createOrUpdateGoogleUser(userInfo)

        // JWT 토큰 생성
        val userDetails: UserDetails = userService.loadUserByUsername(userInfo.email)
        val jwtToken = jwtService.generateToken(userDetails)

        return AuthResponse(
            token = jwtToken,
            userId = user.id ?: 0,
            email = user.email,
            name = user.name
        )
    }

    private fun getUserInfoFromGoogle(idToken: String): GoogleUserInfo {
        return webClient.build()
            .get()
            .uri(googleUserInfoUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $idToken")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<GoogleUserInfo>()
            .block() ?: throw RuntimeException("Failed to retrieve user info from Google")
    }
}