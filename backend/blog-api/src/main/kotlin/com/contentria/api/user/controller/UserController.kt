package com.contentria.api.user.controller

import com.contentria.api.user.controller.response.UserInfoResponse
import com.contentria.api.user.security.CustomUserDetails
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/users")
class UserController {

    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): ResponseEntity<UserInfoResponse> {
        logger.info { "------ /api/users/me start ------" }

        val userDetails = authentication.principal as? CustomUserDetails
            ?: throw IllegalStateException("User details not found in authentication principal")

        val response = UserInfoResponse(
            userId = userDetails.userId,
            email = userDetails.email,
            name = userDetails.displayName,
            profileImage = userDetails.profileImageUrl
        )

        logger.info { "------ /api/users/me end ------" }
        return ResponseEntity.ok(response)
    }
}