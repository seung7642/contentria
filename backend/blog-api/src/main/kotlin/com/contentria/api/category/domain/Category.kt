package com.contentria.api.category.domain

import com.contentria.common.domain.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.util.*

@Entity
@Table(
    name = "categories",
    uniqueConstraints = [UniqueConstraint(columnNames = ["blog_id", "slug"])]
)
class Category(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    var id: UUID? = null,

    @Column(length = 100, nullable = false)
    var name: String,

    @Column(nullable = false)
    var slug: String,

    @Column(nullable = false)
    var displayOrder: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,

    // `ON DELETE SET NULL` 또는 `RESTRICT`와 JPA Cascade 옵션 간의 상호작용은 주의가 필요하다.
    // 데이터베이스가 참조 무결성을 처리하도록 하고 JPA Cascade는 `PERSIST`, `MERGE` 정도로 제한하는 것이 안전하다.
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @BatchSize(size = 100)
    var children: MutableList<Category> = mutableListOf(),

    @Column(name = "blog_id", nullable = false, columnDefinition = "uuid")
    var blogId: UUID,
) : BaseEntity() {

    fun shouldUpdateSlug(newName: String): Boolean {
        return this.name != newName
    }

    fun update(name: String, slug: String, order: Int, parent: Category?) {
        this.name = name
        this.slug = slug
        this.displayOrder = order

        changeParent(parent)
    }

    private fun changeParent(newParent: Category?) {
        if (this.parent == newParent) {
            return
        }

        // 기존 부모와의 관계 끊기 (JPA 양방향 연관관계 관리)
        this.parent?.children?.remove(this)

        this.parent = newParent
        newParent?.children?.add(this)
    }

    companion object {
        fun create(name: String, slug: String, order: Int = 0, parent: Category? = null, blogId: UUID): Category {
            val category = Category(
                name = name,
                slug = slug,
                displayOrder = order,
                parent = null,
                blogId = blogId
            )
            category.changeParent(parent)
            return category
        }
    }
}