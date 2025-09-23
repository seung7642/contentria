package com.contentria.api.post.service

import com.contentria.api.blog.dto.PostSummaryDto
import com.contentria.api.post.repository.PostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository
) {

    @Transactional(readOnly = true)
    fun getPostsByBlogSlug(blogSlug: String, page: Int, size: Int): Page<PostSummaryDto> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        return postRepository.findPostSummariesByBlogSlug(blogSlug, pageable)
    }
}