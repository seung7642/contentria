package com.contentria.api.category

import com.contentria.api.blog.domain.Blog
import com.contentria.api.post.Post
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "categories")
class Category(
    @Id
    @Column(columnDefinition = "UUID")
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    var blog: Blog,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,

    // `ON DELETE SET NULL` 또는 `RESTRICT`와 JPA Cascade 옵션 간의 상호작용은 주의가 필요하다.
    // 데이터베이스가 참조 무결성을 처리하도록 하고 JPA Cascade는 `PERSIST`, `MERGE` 정도로 제한하는 것이 안전하다.
    @OneToMany(mappedBy = "parent", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var children: MutableList<Category> = mutableListOf(),

    @Column(length = 100, nullable = false)
    var name: String,

    @OneToMany(mappedBy = "category", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var posts: MutableList<Post> = mutableListOf(),

    @Column(nullable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now(),
)