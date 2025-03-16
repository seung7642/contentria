package com.demo.blog.auth.controller

import com.demo.blog.auth.dto.AuthResponse
import com.demo.blog.auth.dto.GoogleTokenRequest
import com.demo.blog.auth.service.GoogleAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val googleAuthService: GoogleAuthService) {

    @PostMapping("/google/login")
    fun authenticateGoogleUser(@RequestBody tokenRequest: GoogleTokenRequest): ResponseEntity<AuthResponse> {
        val authResponse = googleAuthService.authenticateGoogleUser(tokenRequest)
        return ResponseEntity.ok(authResponse)
    }
}