package com.demo.blog.auth.controller

import com.demo.blog.auth.service.GoogleAuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/auth")
class AuthController(private val googleAuthService: GoogleAuthService) {

    @GetMapping("/google/callback")
    fun handleGoogleCallback(
        @RequestParam code: String,
        response: HttpServletResponse
    ): ResponseEntity<Void> {
        val authResult = googleAuthService.authenticateWithCode(code)

        val cookie = Cookie("auth_token", authResult.token).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 24 * 60 * 60 // 1 day
        }

        response.addCookie(cookie)

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create("/dashboard"))
            .build()
    }
}