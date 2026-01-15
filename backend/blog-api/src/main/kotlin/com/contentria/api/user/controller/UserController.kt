package com.contentria.api.user.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.user.application.UserService
import com.contentria.api.user.controller.dto.CurrentUserResponse
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
    fun getMyInfo(authentication: Authentication): ResponseEntity<CurrentUserResponse> {
        val userDetails = authentication.principal as? AuthUserDetails
            ?: run {
                log.error { "Authentication principal is not of type CustomUserDetails. It is: ${authentication.principal::class.simpleName}" }
                throw ContentriaException(
                    ErrorCode.UNEXPECTED_AUTHENTICATION_PRINCIPAL
                )
            }

        val userInfo = userService.getCurrentUserInfo(userDetails.userId!!)
        return ResponseEntity.ok(userInfo)
    }
}