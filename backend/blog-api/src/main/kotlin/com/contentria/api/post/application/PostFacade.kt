package com.contentria.api.post.application

import com.contentria.api.blog.application.BlogService
import com.contentria.api.category.application.CategoryService
import com.contentria.api.media.application.MediaImageUrlExtractor
import com.contentria.api.media.application.MediaService
import com.contentria.api.post.application.dto.CreateNewPostCommand
import com.contentria.api.post.application.dto.CreateNewPostInfo
import com.contentria.api.post.application.dto.PostDetailInfo
import com.contentria.api.post.application.dto.PostSummaryInfo
import com.contentria.api.post.application.dto.UpdatePostCommand
import com.contentria.api.post.application.dto.UpdatePostInfo
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.user.application.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {  }

@Component
class PostFacade(
    private val postInternalService: PostInternalService,
    private val postService: PostService,
    private val blogService: BlogService,
    private val categoryService: CategoryService,
    private val userService: UserService,
    private val markdownService: MarkdownService,
    private val mediaService: MediaService,
    private val mediaImageUrlExtractor: MediaImageUrlExtractor
) {
    @Transactional(readOnly = true)
    fun getPostDetail(blogSlug: String, postSlug: String): PostDetailInfo {
        val post = postService.getPublishedPost(blogSlug, postSlug)

        val blogInfo = blogService.getBlogInfo(post.blogId)

        val categoryInfo = post.categoryId?.let { categoryService.getCategoryInfo(it) }

        val authorInfo = userService.getActiveUserInfo(blogInfo.userId)

        return PostDetailInfo.from(
            postContentInfo = post,
            userInfo = authorInfo,
            blogId = blogInfo.blogId,
            blogSlug = blogInfo.slug,
            categoryId = categoryInfo?.id,
            categoryName = categoryInfo?.name
        )
    }

    @Transactional(readOnly = true)
    fun getPostDetail(postId: UUID): PostDetailInfo {
        val post = postService.getPublishedPost(postId)

        val blogInfo = blogService.getBlogInfo(post.blogId)

        val categoryInfo = post.categoryId?.let { categoryService.getCategoryInfo(it) }

        val authorInfo = userService.getActiveUserInfo(blogInfo.userId)

        return PostDetailInfo.from(
            postContentInfo = post,
            userInfo = authorInfo,
            blogId = blogInfo.blogId,
            blogSlug = blogInfo.slug,
            categoryId = categoryInfo?.id,
            categoryName = categoryInfo?.name
        )
    }

    @Transactional(readOnly = true)
    fun getPostsByBlog(
        blogSlug: String,
        categorySlug: String?,
        statuses: Set<PostStatus>,
        pageable: Pageable
    ): Page<PostSummaryInfo> {
        val targetCategoryIds: List<UUID>? = categorySlug?.let {
            val ids = categoryService.getCategoryIdsWithChildren(blogSlug, it)
            if (ids.isEmpty()) {
                return Page.empty(pageable)
            }
            ids
        }

        return postService.getPosts(blogSlug, targetCategoryIds, statuses, pageable)
    }

    @Transactional
    fun createNewPost(userId: UUID, command: CreateNewPostCommand): CreateNewPostInfo {
        blogService.validateBlogOwner(command.blogId, userId)

        categoryService.validateCategoryBelongsToBlog(command.categoryId, command.blogId)

        val summary = markdownService.extractSummary(command.contentMarkdown)

        val savedPost = postInternalService.createPost(
            userId = userId,
            blogId = command.blogId,
            categoryId = command.categoryId,
            title = command.title,
            content = command.contentMarkdown,
            summary = summary,
            status = command.status
        )

        val imageUrls = mediaImageUrlExtractor.extractImageUrls(command.contentMarkdown)
        mediaService.linkMediaToPost(savedPost.id!!, imageUrls)

        log.info { "Post created: postId=${savedPost.id}, blogId=${command.blogId}, userId=$userId" }
        return CreateNewPostInfo.from(savedPost)
    }

    @Transactional
    fun updatePost(userId: UUID, command: UpdatePostCommand): UpdatePostInfo {
        blogService.validateBlogOwner(command.blogId, userId)

        categoryService.validateCategoryBelongsToBlog(command.categoryId, command.blogId)

        val summary = markdownService.extractSummary(command.contentMarkdown)

        val updatedPost = postInternalService.updatePost(
            postId = command.postId,
            blogId = command.blogId,
            categoryId = command.categoryId,
            title = command.title,
            content = command.contentMarkdown,
            summary = summary,
            status = command.status
        )

        val currentImageUrls = mediaImageUrlExtractor.extractImageUrls(command.contentMarkdown)
        val previousMedia = mediaService.findByPostId(command.postId)
        val previousUrls = previousMedia.map { it.publicUrl }

        val newUrls = currentImageUrls.filter { it !in previousUrls }
        val removedUrls = previousUrls.filter { it !in currentImageUrls }

        mediaService.linkMediaToPost(command.postId, newUrls)
        mediaService.unlinkMediaFromPost(command.postId, removedUrls)

        log.info { "Post updated: postId=${updatedPost.id}, blogId=${command.blogId}, userId=$userId" }
        return UpdatePostInfo.from(updatedPost)
    }

    fun deletePost(userId: UUID, postId: UUID) {
        postService.validatePostOwner(userId, postId)
        postService.deletePost(postId)
    }
}