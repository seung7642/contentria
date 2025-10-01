package com.contentria.api.post.controller

import com.contentria.api.post.dto.PostDetailResponse
import com.contentria.api.post.dto.PostSummaryResponse
import com.contentria.api.post.service.PostService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class PostController(
    private val postService: PostService
) {

    @GetMapping("/posts")
    fun getPostsByBlog(
        @RequestParam blogSlug: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostSummaryResponse>> {
        val postsPage = postService.getPostsByBlogSlug(blogSlug, page, size)
        return ResponseEntity.ok(postsPage.map { PostSummaryResponse.from(it) })
    }

    @GetMapping("/blogs/{blogSlug}/posts/{postSlug}")
    fun getPostDetail(
        @PathVariable("blogSlug") blogSlug: String,
        @PathVariable("postSlug") postSlug: String
    ): ResponseEntity<PostDetailResponse> {
        val postDetailAndOwnerInfo = postService.getPostDetail(blogSlug, postSlug)
        return ResponseEntity.ok(PostDetailResponse.from(postDetailAndOwnerInfo))
    }
}