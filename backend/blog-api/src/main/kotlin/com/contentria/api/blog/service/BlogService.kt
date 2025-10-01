package com.contentria.api.blog.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.dto.*
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.Category
import com.contentria.api.category.repository.CategoryRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.config.exception.ErrorCode
import com.contentria.api.post.domain.Post
import com.contentria.api.post.domain.PostStatus
import com.contentria.api.post.repository.PostRepository
import com.contentria.api.user.service.UserService
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.util.*

private val log = KotlinLogging.logger {}

@Service
class BlogService(
    private val blogRepository: BlogRepository,
    private val userService: UserService,
    private val categoryRepository: CategoryRepository,
    private val postRepository: PostRepository
) {

    private val markdownParser: Parser
    private val htmlRenderer: HtmlRenderer

    init {
        val options = MutableDataSet()
        markdownParser = Parser.builder(options).build()
        htmlRenderer = HtmlRenderer.builder(options).build()
    }

    @Transactional
    fun createBlog(userId: UUID, request: CreateBlogCommand): CreateBlogInfo {
        val user = userService.findActiveUserById(userId)

        if (blogRepository.existsBySlug(request.slug)) {
            throw ContentriaException(ErrorCode.DUPLICATE_BLOG_SLUG)
        }

        val savedBlog = blogRepository.save(
            Blog(
                slug = request.slug,
                title = user.email,
                user = user
            )
        )
        addSampleContentFor(savedBlog)

        log.info { "Creating a new blog. email:${user.email}, slug:${savedBlog.slug}" }
        return CreateBlogInfo.from(savedBlog)
    }

    private fun addSampleContentFor(blog: Blog) {
        val techCategory = categoryRepository.save(Category(name = "기술", slug = "tech", blog = blog, parent = null))
        val backendCategory = categoryRepository.save(Category(name = "백엔드", slug = "backend", blog = blog, parent = techCategory))
        val dailyCategory = categoryRepository.save(Category(name = "일상", slug = "daily", blog = blog, parent = null))

        val dailyPostMarkdown = """
                    # ✨ 새로운 시작, 첫 번째 기록
                    
                    | 해당 포스트는 Contentria 블로그의 샘플 기술 포스트입니다.
                    
                    안녕하세요! 저의 생각과 경험을 기록하기 위한 새로운 공간, 저의 첫 블로그에 오신 것을 환영합니다.

                    이곳은 기술적인 내용뿐만 아니라, 저의 소소한 일상과 생각들을 담아내는 공간이 될 예정입니다.
                    
                    ![환영 이미지](https://images.unsplash.com/photo-1557683316-973673baf926?q=80&w=2029&auto=format&fit=crop)
                    *<center>이 블로그의 첫 시작을 알리는 이미지</center>*
                    
                    ### 앞으로 어떤 이야기를 하게 될까요?
                    - 💻 흥미로운 개발 이야기
                    - 📚 인상 깊게 읽은 책 리뷰
                    - ☕ 소소하지만 확실한 행복을 주는 일상 기록
                    
                    ---
                    
                    앞으로 차곡차곡 채워나갈 저의 이야기를 기대해주세요. 방문해주셔서 감사합니다!
                """.trimIndent()
        val backendPostMarkdown = """
                    ## 🚀 코틀린과 함께하는 백엔드 여정
                    
                    | 해당 포스트는 Contentria 블로그의 샘플 기술 포스트입니다.

                    안녕하세요! Contentria 블로그에 작성하는 첫 번째 기술 포스트입니다. 저는 주로 백엔드 개발, 그중에서도 **코틀린(Kotlin)**과 **스프링 부트(Spring Boot)**를 활용한 개발에 대한 이야기를 나누려고 합니다.
        
                    ### 왜 코틀린을 선택했나요?
        
                    코틀린은 여러 가지 매력적인 특징을 가지고 있습니다.
        
                    1.  **간결한 문법**: 자바에 비해 훨씬 적은 코드로 같은 기능을 구현할 수 있습니다.
                    2.  **Null 안정성**: 컴파일 시점에 Null Pointer Exception(NPE)을 방지할 수 있도록 설계되었습니다.
                    3.  **완벽한 상호운용성**: 기존의 자바 코드 및 라이브러리와 100% 호환됩니다.
        
                    아래는 코틀린으로 작성한 간단한 스프링 부트 컨트롤러 예시입니다.
        
                    ```kotlin  
                    @RestController  
                    class HelloController {  
        
                        @GetMapping("/hello")  
                        fun hello(): String {  
                            return "Hello, Contentria!"  
                        }  
                    }  
                    ```
                    
                    이 블로그를 통해 제가 배우고 경험하는 것들을 꾸준히 기록하고 공유하겠습니다. 잘 부탁드립니다!
                """.trimIndent()
        val postsToCreate = listOf(
            Post(
                blog = blog,
                category = backendCategory,
                title = "코틀린(Kotlin)으로 시작하는 나의 첫 백엔드 개발",
                slug = "my-first-backend-with-kotlin",
                contentMarkdown = backendPostMarkdown,
                contentHtml = convertMarkdownToHtml(backendPostMarkdown),
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now().minusDays(1)
            ),
            Post(
                blog = blog,
                category = dailyCategory,
                title = "새로운 시작, 나의 공간에 오신 것을 환영합니다",
                slug = "welcome-to-my-new-space",
                contentMarkdown = dailyPostMarkdown,
                contentHtml = convertMarkdownToHtml(dailyPostMarkdown),
                status = PostStatus.PUBLISHED,
                publishedAt = ZonedDateTime.now()
            )
        )

        postRepository.saveAll(postsToCreate)
    }

    private fun convertMarkdownToHtml(markdown: String): String {
        val document = markdownParser.parse(markdown)
        return htmlRenderer.render(document)
    }

    @Transactional(readOnly = true)
    fun getBlogDetailBySlug(slug: String): BlogLayoutInfo {
        val blog = blogRepository.findBySlug(slug)
            ?: throw ContentriaException(ErrorCode.NOT_FOUND_BLOG)
        val user = blog.user

        val categories = buildCategoryTree(blog)

        return BlogLayoutInfo(
            blog = BlogInfo.from(blog),
            owner = OwnerInfo.from(user),
            categories = categories
        )
    }

    private fun buildCategoryTree(blog: Blog): List<CategoryNodeInfo> {
        val allCategories = categoryRepository.findAllByBlog(blog)

        val postCounts: Map<UUID, Long> = postRepository.countPostsByCategoryId(blog)
            .associate { it.categoryId to it.postCount }

        val totalPostCount = postCounts.values.sum()

        // to는 Pair를 생성, associate는 Pair 리스트를 Map으로 변환
        val nodeMap = allCategories.associate {
            it.id to CategoryNodeInfo(it.id, it.name, it.slug, postCounts[it.id] ?: 0, mutableListOf())
        }

        val rootNodes = mutableListOf<CategoryNodeInfo>()
        allCategories.forEach { category ->
            val node = nodeMap.getValue(category.id)

            if (category.parent == null) {
                rootNodes.add(node)
            } else {
                val parentNode = nodeMap[category.parent!!.id]
                (parentNode?.children as? MutableList)?.add(node)
            }
        }

        rootNodes.forEach { updateTotalPostCount(it) }

        val allViewCategory = CategoryNodeInfo(null, "전체보기", "total", totalPostCount, emptyList())
        return listOf(allViewCategory) + rootNodes
    }

    private fun updateTotalPostCount(node: CategoryNodeInfo): Long {
        if (node.children.isEmpty()) {
            return node.postCount
        }
        val childrenTotal = node.children.sumOf { updateTotalPostCount(it) }
        node.postCount += childrenTotal
        return node.postCount
    }
}