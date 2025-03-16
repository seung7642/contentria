package com.demo.blog.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.demo.blog.auth.dto.AuthResponse
import com.demo.blog.auth.dto.GoogleTokenRequest
import com.demo.blog.auth.dto.GoogleUserInfo
import com.demo.blog.common.security.JwtService
import com.demo.blog.user.service.CustomerUserDetailsService
import com.demo.blog.user.service.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class GoogleAuthService(
    private val userService: UserService,
    private val customerUserDetailsService: CustomerUserDetailsService,
    private val jwtService: JwtService,
) {

    fun authenticateGoogleUser(tokenRequest: GoogleTokenRequest): AuthResponse {
        // Google ID 토큰을 사용하여 사용자 정보 가져오기
        val userInfo = getUserInfoFromGoogle(tokenRequest.credential)

        // 사용자 정보 저장 또는 업데이트
        val user = userService.createOrUpdateGoogleUser(userInfo)

        // JWT 토큰 생성
        val userDetails: UserDetails = customerUserDetailsService.loadUserByUsername(userInfo.email)
        val jwtToken = jwtService.generateToken(userDetails)

        return AuthResponse(
            token = jwtToken,
            userId = user.id,
            email = user.email,
            name = user.username
        )
    }

    private fun getUserInfoFromGoogle(idToken: String): GoogleUserInfo {
        try {
            // ID 토큰 디코딩 (서명 검증 없이)
            val jwt: DecodedJWT = JWT.decode(idToken)

            // 페이로드에서 사용자 정보 추출
            return GoogleUserInfo(
                id = jwt.subject,
                email = jwt.getClaim("email").asString(),
                name = jwt.getClaim("name").asString(),
                picture = jwt.getClaim("picture").asString()
            )
        } catch (e: Exception) {
            throw RuntimeException("Invalid ID token", e)
        }
    }
}