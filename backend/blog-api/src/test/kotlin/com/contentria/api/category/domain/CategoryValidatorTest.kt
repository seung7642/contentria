package com.contentria.api.category.domain

import com.contentria.api.global.error.ContentriaException
import com.contentria.api.global.error.ErrorCode
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class CategoryValidatorTest {

    private val validator = CategoryValidator()

    @Test
    @DisplayName("부모 카테고리가 삭제될 때, 자식이 여전히 그 부모를 참조하려고 하면 예외가 발생해야 한다.")
    fun should_ThrowException_when_SurvivingChild_References_DeletedParent() {
        val parent = createCategory(id = "parent-uuid", parent = null)
        val child = createCategory(id = "child-uuid", parent = parent)

        val toDelete = listOf(parent)

        val futureParentMap = mapOf(child.id.toString() to parent.id.toString())

        assertThatThrownBy { validator.validateReferentialIntegrity(toDelete, futureParentMap) }
            .isInstanceOf(ContentriaException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_CATEGORY)
    }

    @Test
    @DisplayName("부모 카테고리가 삭제되더라도, 자식이 독립(Root)하거나 다른 부모로 이동하면 검증을 통과해야 한다.")
    fun should_Pass_when_Child_Moves_Or_BecomesRoot() {
        val parent = createCategory(id = "parent-uuid", parent = null)
        val child = createCategory(id = "child-uuid", parent = parent)

        val toDelete = listOf(parent)

        val futureParentMap = mapOf(
            child.id.toString() to null
        )

        // No exception should be thrown
        assertThatCode { validator.validateReferentialIntegrity(toDelete, futureParentMap) }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("부모와 자식 카테고리가 모두 삭제되는 경우(Cascading Delete)에는 검증을 통과해야 한다.")
    fun should_Pass_when_ParentAndChild_BothDeleted() {
        val parent = createCategory(id = "parent-uuid", parent = null)
        val child = createCategory(id = "child-uuid", parent = parent)

        val toDelete = listOf(parent, child)

        val futureParentMap = emptyMap<String, String?>()

        // No exception should be thrown
        assertThatCode { validator.validateReferentialIntegrity(toDelete, futureParentMap) }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("자식이 없는 독립적인 카테고리를 삭제할 때는 검증을 통과해야 한다.")
    fun should_Pass_when_IndependentCategoryDeleted() {
        val category = createCategory(id = "single-uuid", parent = null)

        val toDelete = listOf(category)

        val futureParentMap = emptyMap<String, String?>()

        assertThatCode { validator.validateReferentialIntegrity(toDelete, futureParentMap) }
            .doesNotThrowAnyException()
    }

    private fun createCategory(
        id: String = UUID.randomUUID().toString(),
        parent: Category? = null
    ): Category {
        val category = Category.create(
            name = "Test Category",
            slug = "test-slug",
            order = 0,
            parent = parent,
            blogId = UUID.randomUUID()
        )

        setEntityId(category, parseUuid(id))

        return category
    }

    private fun setEntityId(entity: Any, id: UUID) {
        val field = entity.javaClass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }

    private fun parseUuid(id: String): UUID {
        return try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            UUID.nameUUIDFromBytes(id.toByteArray())
        }
    }
}