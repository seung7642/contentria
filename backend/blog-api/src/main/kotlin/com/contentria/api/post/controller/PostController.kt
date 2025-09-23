package com.contentria.api.post.controller

import com.contentria.api.blog.dto.PostSummaryDto
import com.contentria.api.post.service.PostService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val postService: PostService
) {

    @GetMapping
    fun getPostsByBlog(
        @RequestParam blogSlug: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostSummaryDto>> {
        val postsPage = postService.getPostsByBlogSlug(blogSlug, page, size)
        return ResponseEntity.ok(postsPage)
    }
}