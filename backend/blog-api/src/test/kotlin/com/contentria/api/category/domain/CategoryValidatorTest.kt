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
    @DisplayName("부모 카테고리를 삭제할 때, 자식 카테고리가 삭제 목록에 없으면 예외가 발생해야 한다.")
    fun should_ThrowException_when_ParentDeleted_But_ChildIsNot() {
        val parent = createCategory(id = "00000000-0000-0000-0000-000000000001", parent = null)
        val child = createCategory(id = "00000000-0000-0000-0000-000000000002", parent = parent)

        val toDelete = listOf(parent)
        val requestIds = setOf(parent.id.toString())

        assertThatThrownBy {
            validator.validateInternalRules(toDelete, requestIds)
        }
            .isInstanceOf(ContentriaException::class.java)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_CATEGORY)
    }

    @Test
    @DisplayName("부모와 자식 카테고리를 모두 삭제 목록에 포함하면 검증을 통과해야 한다.")
    fun should_Pass_when_ParentAndChild_BothDeleted() {
        val parent = createCategory(id = "00000000-0000-0000-0000-000000000001", parent = null)
        val child = createCategory(id = "00000000-0000-0000-0000-000000000002", parent = parent)

        val toDelete = listOf(parent, child)
        val requestIds = setOf(parent.id.toString(), child.id.toString())

        // No exception should be thrown
        assertThatCode {
            validator.validateInternalRules(toDelete, requestIds)
        }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("자식만 삭제하는 경우에는 부모가 존재하더라도 검증을 통과해야 한다.")
    fun should_Pass_when_OnlyChildDeleted() {
        val parent = createCategory(id = "00000000-0000-0000-0000-000000000001", parent = null)
        val child = createCategory(id = "00000000-0000-0000-0000-000000000002", parent = parent)

        val toDelete = listOf(child)
        val requestIds = setOf(child.id.toString())

        // No exception should be thrown
        assertThatCode {
            validator.validateInternalRules(toDelete, requestIds)
        }
            .doesNotThrowAnyException()
    }

    @Test
    @DisplayName("자식이 없는 독립적인 카테고리를 삭제할 때는 검증을 통과해야 한다.")
    fun should_Pass_when_IndependentCategoryDeleted() {
        val category = createCategory(id = "00000000-0000-0000-0000-000000000001", parent = null)

        val toDelete = listOf(category)
        val requestIds = setOf(category.id.toString())

        // No exception should be thrown
        assertThatCode {
            validator.validateInternalRules(toDelete, requestIds)
        }
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

        setEntityId(category, UUID.fromString(id))

        return category
    }

    private fun setEntityId(entity: Any, id: UUID) {
        val field = entity.javaClass.getDeclaredField("id")
        field.isAccessible = true
        field.set(entity, id)
    }
}