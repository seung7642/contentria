package com.contentria.api.user.controller

import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.user.security.CustomUserDetails
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): ResponseEntity<UserInfoResponse> {
        val userDetails = authentication.principal as? CustomUserDetails
            ?: run {
                log.error { "Authentication principal is not of type CustomUserDetails. It is: ${authentication.principal::class.simpleName}" }
                throw ContentriaException(ErrorCode.UNEXPECTED_AUTHENTICATION_PRINCIPAL)
            }

        val userInfo = userService.getCurrentUserInfo(userDetails.userId)
        return ResponseEntity.ok(userInfo)
    }
}