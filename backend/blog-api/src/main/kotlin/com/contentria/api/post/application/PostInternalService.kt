package com.contentria.api.post.application

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostRepository
import com.contentria.api.post.domain.PostSlugGenerator
import com.contentria.api.post.domain.PostStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.UUID

@Service
class PostInternalService(
    private val postRepository: PostRepository,
    private val postSlugGenerator: PostSlugGenerator
) {
    @Transactional
    fun createPost(
        userId: UUID,
        blogId: UUID,
        categoryId: UUID,
        title: String,
        content: String,
        summary: String,
        status: PostStatus
    ): Post {
        val uniqueSlug = postSlugGenerator.generate(blogId, title)

        val newPost = Post.create(
            userId = userId,
            blogId = blogId,
            categoryId = categoryId,
            slug = uniqueSlug,
            title = title,
            content = content,
            summary = summary,
            metaTitle = title,
            metaDescription = summary,
            status = status,
            publishedAt = if (status.isPublished()) ZonedDateTime.now() else null
        )

        return postRepository.save(newPost)
    }

    @Transactional
    fun updatePost(
        postId: UUID,
        blogId: UUID,
        categoryId: UUID,
        title: String,
        content: String,
        summary: String,
        status: PostStatus
    ): Post {
        val post = postRepository.findById(postId) ?: throw ContentriaException(ErrorCode.NOT_FOUND_POST)
        val uniqueSlug = postSlugGenerator.generate(blogId, title)

        post.update(
            title = title,
            slug = uniqueSlug,
            content = content,
            summary = summary,
            status = status,
            categoryId = categoryId
        )

        return post
    }
}