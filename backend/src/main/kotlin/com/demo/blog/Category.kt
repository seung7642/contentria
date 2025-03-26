package com.demo.blog

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    val blog: Blog,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    val parent: Category? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL])
    val children: MutableList<Category> = mutableListOf(),

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val slug: String,

    val description: String? = null,

    @Column(name = "order_position")
    val orderPosition: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(mappedBy = "categories")
    val posts: MutableSet<Post> = mutableSetOf()
)