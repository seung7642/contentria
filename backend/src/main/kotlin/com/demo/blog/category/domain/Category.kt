package com.demo.blog.category.domain

import com.demo.blog.blog.domain.Blog
import com.demo.blog.post.domain.Post
import jakarta.persistence.*
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