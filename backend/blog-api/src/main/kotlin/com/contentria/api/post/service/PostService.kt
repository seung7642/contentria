package com.contentria.api.post.service

import com.contentria.api.blog.dto.OwnerInfo
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.dto.PostDetailAndOwnerInfo
import com.contentria.api.post.dto.PostDetailInfo
import com.contentria.api.post.dto.PostSummaryInfo
import com.contentria.api.post.repository.PostRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PostService(
    private val postRepository: PostRepository,
) {

    fun getPostsByBlogSlug(blogSlug: String, page: Int, size: Int): Page<PostSummaryInfo> {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"))
        val postSummaries = postRepository.findPostSummariesByBlogSlug(blogSlug, pageable)
        return postSummaries.map { PostSummaryInfo.from(it) }
    }

    fun getPostDetail(blogSlug: String, postSlug: String): PostDetailAndOwnerInfo {
        val post = postRepository.findPublishedByBlogsWithDetails(blogSlug, postSlug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)

        return PostDetailAndOwnerInfo(
            post = PostDetailInfo.from(post),
            owner = OwnerInfo.from01(post.blog.user)
        )
    }
}