package com.contentria.api.category.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.category.application.CategoryFacade
import com.contentria.api.category.application.CategoryService
import com.contentria.api.category.controller.dto.CategoryResponse
import com.contentria.api.category.controller.dto.SyncCategoryRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryFacade: CategoryFacade,
    private val categoryService: CategoryService,
) {

    @GetMapping
    fun getCategories(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @RequestParam blogId: UUID
    ): ResponseEntity<List<CategoryResponse>> {
        val categoryInfos = categoryFacade.getCategories(
            userId = userDetails.userId,
            blogId = blogId
        )
        val responses = categoryInfos.map { CategoryResponse.from(it) }
        return ResponseEntity.ok(responses)
    }

    @PostMapping
    fun syncCategory(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable blogId: UUID,
        @RequestBody @Valid request: List<SyncCategoryRequest>
    ): ResponseEntity<Void> {
        val actorUserId = userDetails.userId
        categoryService.syncCategories(blogId, request.map { it.toCommand(actorUserId!!) })
        return ResponseEntity.ok().build()
    }
}