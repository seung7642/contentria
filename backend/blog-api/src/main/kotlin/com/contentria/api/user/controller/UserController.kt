package com.contentria.api.user.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.user.application.UserFacade
import com.contentria.api.user.controller.dto.CurrentUserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/users")
class UserController(
    private val userFacade: UserFacade
) {

    @GetMapping("/me")
    fun getMyInfo(@AuthenticationPrincipal userDetails: AuthUserDetails): ResponseEntity<CurrentUserResponse> {
        val userInfo = userFacade.getCurrentUserInfo(userDetails.userId)
        return ResponseEntity.ok(userInfo)
    }
}