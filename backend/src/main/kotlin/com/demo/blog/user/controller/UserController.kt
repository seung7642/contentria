package com.demo.blog.user.controller

import com.demo.blog.user.controller.response.UserInfoResponse
import com.demo.blog.user.security.CustomUserDetails
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController {

    // JwtAuthenticationFilter가 SecurityContextHolder에 설정한 Authentication 객체를 주입받는다.
    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): ResponseEntity<UserInfoResponse> {
        val userDetails = authentication.principal as? CustomUserDetails
            ?: throw IllegalStateException("User details not found in authentication principal")

        val response = UserInfoResponse(
            userId = userDetails.userId,
            email = userDetails.email,
            name = userDetails.displayName,
            profileImage = userDetails.profileImageUrl
        )

        return ResponseEntity.ok(response)
    }
}