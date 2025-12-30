package com.contentria.api.post.controller

import com.contentria.api.post.dto.CreateNewPostRequest
import com.contentria.api.post.dto.CreateNewPostResponse
import com.contentria.api.post.dto.PostDetailResponse
import com.contentria.api.post.dto.PostSummaryResponse
import com.contentria.api.post.service.PostService
import com.contentria.api.user.security.CustomUserDetails
import com.contentria.common.aop.ApiLog
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping
class PostController(
    private val postService: PostService
) {

    @ApiLog
    @GetMapping("/posts")
    fun getPostsByBlog(
        @RequestParam blogSlug: String,
        @PageableDefault(sort = ["publishedAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<PostSummaryResponse>> {
        val postsPage = postService.getPostsByBlogSlug(blogSlug, pageable)
        return ResponseEntity.ok(postsPage.map { PostSummaryResponse.from(it) })
    }

    @ApiLog
    @GetMapping("/blogs/{blogSlug}/posts/{postSlug}")
    fun getPostDetail(
        @PathVariable blogSlug: String,
        @PathVariable postSlug: String
    ): ResponseEntity<PostDetailResponse> {
        val postDetailAndOwnerInfo = postService.getPostDetail(blogSlug, postSlug)
        return ResponseEntity.ok(PostDetailResponse.from(postDetailAndOwnerInfo))
    }

    @PostMapping("/posts")
    fun createNewPost(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody request: CreateNewPostRequest
    ): ResponseEntity<CreateNewPostResponse> {
        log.info { "Creating new post for userId=${userDetails.userId}, request=$request" }
        val createNewPostInfo = postService.createNewPost(userDetails.userId!!, request.toCommand())
        return ResponseEntity.ok(CreateNewPostResponse.from(createNewPostInfo))
    }
}