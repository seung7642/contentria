package com.contentria.api.category.service

import com.contentria.api.blog.domain.Blog
import com.contentria.api.blog.repository.BlogRepository
import com.contentria.api.category.application.CategoryService
import com.contentria.api.category.domain.Category
import com.contentria.api.category.application.dto.SyncCategoryCommand
import com.contentria.api.category.domain.CategoryRepository
import com.contentria.api.config.exception.ContentriaException
import com.contentria.api.user.domain.User
import com.contentria.api.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@Transactional
class CategoryServiceTest {

    @Autowired private lateinit var categoryService: CategoryService
    @Autowired private lateinit var categoryRepository: CategoryRepository
    @Autowired private lateinit var blogRepository: BlogRepository
    @Autowired private lateinit var userRepository: UserRepository

    private lateinit var user: User
    private lateinit var blog: Blog

    @BeforeEach
    fun setUp() {
        user = User.createEmailUser(
            email = "test@example.com",
            username = "test01",
            password = "password01"
        )
        userRepository.save(user)

        blog = Blog(
            slug = "test-blog",
            title = "Test Blog",
            user = user
        )
        blogRepository.save(blog)
    }

    @Test
    @DisplayName("정상적인 요청으로 카테고리가 생성된다.")
    fun createCategories() {
        val command01 = createCommand(name = "Backend", order = 1)
        val command02 = createCommand(name = "Frontend", order = 2)

        categoryService.syncCategories(blog.id!!, listOf(command01, command02))

        val savedCategories = categoryRepository.findAll(blog)
        assertThat(savedCategories).hasSize(2)
        assertThat(savedCategories).extracting("name")
            .containsExactlyInAnyOrder("Backend", "Frontend")
    }

    @Test
    @DisplayName("요청 리스트에 없는 카테고리는 삭제된다.")
    fun deleteMissingCategories() {
        val category01 = categoryRepository.save(Category.create(
            name = "Backend",
            slug = "backend",
            blog = blog,
            parent = null,
            order = 1
        ))
        val category02 = categoryRepository.save(Category.create(
            name = "Frontend",
            slug = "frontend",
            blog = blog,
            parent = null,
            order = 2
        ))

        val command = createCommand(id = category01.id!!, name = "Backend", order = 1)
        categoryService.syncCategories(blog.id!!, listOf(command))

        val remaining = categoryRepository.findAll(blog)
        assertThat(remaining).hasSize(1)
        assertThat(remaining[0].id).isEqualTo(category01.id)
    }

    @Test
    @DisplayName("부모-자식 관계가 올바르게 설정된다.")
    fun syncParentChildRelationship() {
        val parentId = UUID.randomUUID()
        val childId = UUID.randomUUID()
        val parentCmd = createCommand(id = parentId, name = "Parent", order = 1)
        val childCmd = createCommand(id = childId, name = "Child", parentId = parentId, order = 2)

        categoryService.syncCategories(blog.id!!, listOf(parentCmd, childCmd))

        val categories = categoryRepository.findAll(blog)
        val savedParent = categories.find { it.name == "Parent" }
            ?: throw IllegalStateException("Parent not found")
        val savedChild = categories.find { it.name == "Child" }
            ?: throw IllegalStateException("Child not found")

        assertThat(savedChild.parent).isNotNull
        assertThat(savedChild.parent!!.id).isEqualTo(savedParent.id)
    }

    @Test
    @DisplayName("자식이 있는 부모 카테고리를 삭제 시 예외가 발생한다.")
    fun preventOrphanRemoval() {
        val parent = categoryRepository.save(Category.create(
            name = "Parent",
            slug = "parent",
            blog = blog,
            parent = null,
            order = 1
        ))
        val child = categoryRepository.save(Category.create(
            name = "Child",
            slug = "child",
            blog = blog,
            parent = null,
            order = 2
        ))

        val childCommand = createCommand(id = child.id!!, name = "Child", parentId = parent.id, order = 1)

        assertThatThrownBy {
            categoryService.syncCategories(blog.id!!, listOf(childCommand))
        }
            .isInstanceOf(ContentriaException::class.java)
    }

    private fun createCommand(
        id: UUID = UUID.randomUUID(),
        actorUserId: UUID = user.id!!,
        name: String,
        parentId: UUID? = null,
        order: Int
    ): SyncCategoryCommand {
        return SyncCategoryCommand(
            actorUserId = actorUserId,
            id = id.toString(),
            name = name,
            parentId = parentId?.toString(),
            order = order
        )
    }
}