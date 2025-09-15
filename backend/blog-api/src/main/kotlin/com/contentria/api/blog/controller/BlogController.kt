package com.contentria.api.blog.controller

import com.contentria.api.blog.dto.BlogResponse
import com.contentria.api.blog.dto.CreateBlogRequest
import com.contentria.api.blog.service.BlogService
import com.contentria.api.user.security.CustomUserDetails
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/blogs")
class BlogController(
    private val blogService: BlogService
) {

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    fun createBlog(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody request: CreateBlogRequest
    ): ResponseEntity<BlogResponse> {
        val userId = userDetails.userId
        val blogResponse = blogService.createBlog(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(blogResponse)
    }
}