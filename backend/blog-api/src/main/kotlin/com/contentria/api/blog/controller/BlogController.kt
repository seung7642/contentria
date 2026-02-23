package com.contentria.api.blog.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.blog.application.BlogFacade
import com.contentria.api.blog.controller.dto.BlogLayoutResponse
import com.contentria.api.blog.controller.dto.BlogResponse
import com.contentria.api.blog.controller.dto.CreateBlogRequest
import com.contentria.api.blog.controller.dto.CreateBlogResponse
import com.contentria.common.global.error.ContentriaException
import com.contentria.common.global.error.ErrorCode
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.Locale

@RestController
@RequestMapping("/blogs")
class BlogController(
    private val blogFacade: BlogFacade
) {
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getMyBlog(@AuthenticationPrincipal userDetails: AuthUserDetails): ResponseEntity<List<BlogResponse>> {
        val userId = userDetails.userId

        val blogInfo = blogFacade.getMyBlog(userId)
        return ResponseEntity.ok(blogInfo.map { BlogResponse.from(it) })
    }

    @GetMapping("/layout/{slug}")
    fun getBlogBySlug(
        @PathVariable slug: String,
        @PageableDefault(size = 10, sort = ["publishedAt"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<BlogLayoutResponse> {
        val blogDetail = blogFacade.getBlogLayout(slug)
        return ResponseEntity.ok(BlogLayoutResponse.from(blogDetail))
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    fun createBlog(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @Valid @RequestBody request: CreateBlogRequest
    ): ResponseEntity<CreateBlogResponse> {
        val userId = requireNotNull(userDetails.userId) {
            "Authenticated user must have a valid user ID."
        }

        val set = setOf("administrator", "admin", "notice")
        if (request.slug.lowercase(Locale.getDefault()) in set) {
            throw ContentriaException(ErrorCode.NOT_ALLOWED_BLOG_NAME)
        }

        val blogResponse = blogFacade.createBlogWithSamples(userId, request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateBlogResponse.from(blogResponse))
    }
}