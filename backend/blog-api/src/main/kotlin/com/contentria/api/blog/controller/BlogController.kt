package com.contentria.api.blog.controller

import com.contentria.api.blog.dto.BlogLayoutResponse
import com.contentria.api.blog.dto.CreateBlogRequest
import com.contentria.api.blog.dto.CreateBlogResponse
import com.contentria.api.blog.service.BlogService
import com.contentria.api.user.security.CustomUserDetails
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

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
    ): ResponseEntity<CreateBlogResponse> {
        val userId = requireNotNull(userDetails.userId) {
            "Authenticated user must have a valid user ID."
        }
        val blogResponse = blogService.createBlog(userId, request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateBlogResponse.from(blogResponse))
    }

    @GetMapping("/layout/{slug}")
    fun getBlogBySlug(
        @PathVariable slug: String,
        @PageableDefault(size = 10, sort = ["publishedAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<BlogLayoutResponse> {
        val blogDetail = blogService.getBlogDetailBySlug(slug)
        return ResponseEntity.ok(BlogLayoutResponse.from(blogDetail))
    }
}