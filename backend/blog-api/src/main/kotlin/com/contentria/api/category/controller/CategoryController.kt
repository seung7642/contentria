package com.contentria.api.category.controller

import com.contentria.api.blog.service.BlogService
import com.contentria.api.category.controller.dto.CategoryResponse
import com.contentria.api.category.controller.dto.SyncCategoryRequest
import com.contentria.api.category.application.CategoryService
import com.contentria.api.user.security.CustomUserDetails
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService,
    private val blogService: BlogService
) {

    @GetMapping
    fun getCategories(@RequestParam blogId: UUID): ResponseEntity<List<CategoryResponse>> {
        val blog = blogService.getBlogByBlogId(blogId)
        val categories = categoryService.getFlattenedCategories(blog)
        val responses = categories.map { CategoryResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @PostMapping
    fun createCategory(
        @PathVariable blogId: UUID,
        @RequestBody @Valid request: List<SyncCategoryRequest>,
        @AuthenticationPrincipal userPrincipal: CustomUserDetails
    ): ResponseEntity<Void> {
        val actorUserId = userPrincipal.userId
        categoryService.syncCategories(blogId, request.map { it.toCommand(actorUserId!!) })
        return ResponseEntity.ok().build()
    }
}