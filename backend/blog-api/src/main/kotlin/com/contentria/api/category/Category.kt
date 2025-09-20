package com.contentria.api.category

import com.contentria.api.blog.domain.Blog
import com.contentria.api.user.domain.BaseEntity
import com.contentria.common.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "categories")
class Category(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    var id: UUID? = null,

    @Column(length = 100, nullable = false)
    var name: String,

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