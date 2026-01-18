package com.contentria.api.post.application

import com.contentria.api.blog.application.BlogService
import com.contentria.api.category.application.CategoryService
import com.contentria.api.post.application.dto.CreateNewPostCommand
import com.contentria.api.post.application.dto.CreateNewPostInfo
import com.contentria.api.post.application.dto.PostDetailInfo
import com.contentria.api.user.application.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val log = KotlinLogging.logger {  }

@Component
class PostFacade(
    private val postService: PostService,
    private val blogService: BlogService,
    private val categoryService: CategoryService,
    private val userService: UserService,
    private val markdownService: MarkdownService
) {
    @Transactional
    fun createNewPost(userId: UUID, command: CreateNewPostCommand): CreateNewPostInfo {
        blogService.validateBlogOwner(command.blogId, userId)

        categoryService.validateCategoryBelongsToBlog(command.categoryId, command.blogId)

        val summary = markdownService.extractSummary(command.contentMarkdown)

        val savedPost = postService.createPost(
            blogId = command.blogId,
            categoryId = command.categoryId,
            title = command.title,
            content = command.contentMarkdown,
            summary = summary,
            status = command.status
        )

        log.info { "Created new post: ${savedPost.id}" }
        return CreateNewPostInfo.from(savedPost)
    }

    @Transactional(readOnly = true)
    fun getPostDetail(blogSlug: String, postSlug: String): PostDetailInfo {
        val post = postService.getPublishedPost(blogSlug, postSlug)

        val blogInfo = blogService.getBlogInfo(post.blogId)

        val categoryInfo = post.categoryId?.let { categoryService.getCategoryInfo(it) }

        val authorInfo = userService.getActiveUserInfo(blogInfo.userId)

        return PostDetailInfo.from(post, authorInfo, blogInfo.slug, categoryInfo?.name)
    }
}