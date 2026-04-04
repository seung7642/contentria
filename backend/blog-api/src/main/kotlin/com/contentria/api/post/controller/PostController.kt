package com.contentria.api.post.controller

import com.contentria.api.auth.infrastructure.security.AuthUserDetails
import com.contentria.api.post.application.PostFacade
import com.contentria.api.post.controller.dto.*
import com.contentria.api.post.domain.PostStatus
import com.contentria.common.global.aop.ApiLog
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping
class PostController(
    private val postFacade: PostFacade
) {

    @ApiLog
    @GetMapping("/blogs/{blogSlug}/posts")
    fun getPostsByBlog(
        @PathVariable blogSlug: String,
        @RequestParam(name = "category", required = false) categorySlug: String?,
        @RequestParam(required = false) statuses: Set<PostStatus> = setOf(PostStatus.PUBLISHED),
        @PageableDefault(size = 10) pageable: Pageable
    ): ResponseEntity<Page<PostSummaryResponse>> {
        val postsPage = postFacade.getPostsByBlog(blogSlug, categorySlug, statuses, pageable)
        return ResponseEntity.ok(postsPage.map { PostSummaryResponse.from(it) })
    }

    @ApiLog
    @GetMapping("/blogs/{blogSlug}/posts/{postSlug}")
    fun getPostDetail(
        @PathVariable blogSlug: String,
        @PathVariable postSlug: String
    ): ResponseEntity<PostDetailResponse> {
        val postDetailAndOwnerInfo = postFacade.getPostDetail(blogSlug, postSlug)
        return ResponseEntity.ok(PostDetailResponse.from(postDetailAndOwnerInfo))
    }

    @ApiLog
    @GetMapping("/posts/{postId}")
    fun getPostDetail(
        @PathVariable postId: UUID
    ): ResponseEntity<PostDetailResponse> {
        val postDetailAndOwnerInfo = postFacade.getPostDetail(postId)
        return ResponseEntity.ok(PostDetailResponse.from(postDetailAndOwnerInfo))
    }

    @PostMapping("/posts")
    fun createNewPost(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @Valid @RequestBody request: CreateNewPostRequest
    ): ResponseEntity<CreateNewPostResponse> {
        log.debug { "Creating post: userId=${userDetails.userId}, blogId=${request.blogId}" }
        val createNewPostInfo = postFacade.createNewPost(userDetails.userId, request.toCommand())
        return ResponseEntity.ok(CreateNewPostResponse.from(createNewPostInfo))
    }

    @PostMapping("/posts/{postId}")
    fun updatePost(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable postId: UUID,
        @Valid @RequestBody request: UpdatePostRequest
    ): ResponseEntity<UpdatePostResponse> {
        log.debug { "Updating post: postId=$postId, userId=${userDetails.userId}" }
        val updatePostInfo = postFacade.updatePost(userDetails.userId, request.toCommand())
        return ResponseEntity.ok(UpdatePostResponse.from(updatePostInfo))
    }

    @DeleteMapping("/posts/{postId}")
    fun deletePost(
        @AuthenticationPrincipal userDetails: AuthUserDetails,
        @PathVariable postId: UUID
    ): ResponseEntity<Void> {
        log.debug { "Deleting post: postId=$postId, userId=${userDetails.userId}" }
        postFacade.deletePost(userDetails.userId, postId)
        return ResponseEntity.noContent().build()
    }
}