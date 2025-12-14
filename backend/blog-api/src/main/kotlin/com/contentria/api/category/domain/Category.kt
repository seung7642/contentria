package com.contentria.api.category.domain

import com.contentria.api.blog.domain.Blog
import com.contentria.api.user.domain.BaseEntity
import com.contentria.common.config.jpa.GeneratedUuidV7
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,

    // `ON DELETE SET NULL` 또는 `RESTRICT`와 JPA Cascade 옵션 간의 상호작용은 주의가 필요하다.
    // 데이터베이스가 참조 무결성을 처리하도록 하고 JPA Cascade는 `PERSIST`, `MERGE` 정도로 제한하는 것이 안전하다.
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var children: MutableList<Category> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    var blog: Blog,
) : BaseEntity()