package com.contentria.api.blog.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.dto.*
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.repository.CategoryRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.Post
import com.contentria.api.post.repository.PostRepository
import com.contentria.api.post.PostStatus
import com.contentria.api.user.service.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.UUID

private val log = KotlinLogging.logger {}

@Service
class BlogService(
    private val blogRepository: BlogRepository,
    private val userService: UserService,
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository
) {

    @Transactional
    fun createBlog(userId: UUID, request: CreateBlogRequest): BlogResponse {
        val user = userService.findActiveUserById(userId)

        if (blogRepository.existsBySlug(request.slug)) {
            throw ContentriaException(ErrorCode.DUPLICATE_BLOG_SLUG)
        }

        val newBlog = blogRepository.save(
            Blog(
                slug = request.slug,
                title = user.email,
                user = user
            )
        )

        val savedBlog = blogRepository.save(newBlog)

        log.info { "Creating a new blog. email:${user.email}, slug:${savedBlog.slug}" }
        return BlogResponse.from(savedBlog)
    }

    fun findSlugsByUserId(userId: UUID): List<String> {
        return blogRepository.findSlugsByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun getBlogDetailBySlug(slug: String, pageable: PageRequest): BlogDetailResponse {
        // 1. 블로그와 소유자 정보 조회 (없으면 404 에러)
        val blog = blogRepository.findBySlug(slug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)
        val user = blog.user

        // 2. 카테고리 트리 구조 생성
        val categories = buildCategoryTree(blog)

        // 3. 게시글 목록 페이지네이션으로 조회 및 DTO 변환
        val postsPage = postRepository.findAllByBlogAndStatus(blog, PostStatus.PUBLISHED, pageable)
            .map { it.toSummaryDto() }

        // 4. 최종 응답 DTO 조립
        return BlogDetailResponse(
            blogInfo = blog.toInfoDto(),
            ownerInfo = OwnerInfoDto.from(user),
            categories = categories,
            posts = postsPage
        )
    }

    private fun buildCategoryTree(blog: Blog): List<CategoryNodeDto> {
        val allCategories = categoryRepository.findAllByBlog(blog)

        val postCounts: Map<UUID, Long> = postRepository.countPostsByCategoryId(blog)
            .associate { it.categoryId to it.postCount }

        val totalPostCount = postCounts.values.sum()

        val nodeMap = allCategories.associate {
            it.id to CategoryNodeDto(it.id, it.name, postCounts[it.id] ?: 0, mutableListOf())
        }

        val rootNodes = mutableListOf<CategoryNodeDto>()
        allCategories.forEach { category ->
            val node = nodeMap[category.id]!!
            if (category.parent == null) {
                rootNodes.add(node)
            } else {
                val parentNode = nodeMap[category.parent!!.id]
                (parentNode?.children as? MutableList)?.add(node)
            }
        }

        rootNodes.forEach { updateTotalPostCount(it) }

        val allViewCategory = CategoryNodeDto(null, "전체보기", totalPostCount, emptyList())
        return listOf(allViewCategory) + rootNodes
    }

    private fun updateTotalPostCount(node: CategoryNodeDto): Long {
        if (node.children.isEmpty()) {
            return node.postCount
        }
        val childrenTotal = node.children.sumOf { updateTotalPostCount(it) }
        node.postCount += childrenTotal
        return node.postCount
    }

    // Entity -> DTO 변환을 위한 확장 함수
    private fun Blog.toInfoDto() = BlogInfoDto(this.title, this.slug, this.description)
    private fun Post.toSummaryDto() = PostSummaryDto(
        id = this.id!!,
        slug = this.slug,
        title = this.title,
        excerpt = this.contentMarkdown.take(150) + if (this.contentMarkdown.length > 150) "..." else "",
        featuredImageUrl = this.featuredImageUrl,
        publishedAt = this.publishedAt ?: ZonedDateTime.now(),
        categoryName = this.category?.name,
        likeCount = 0
    )
}