package com.demo.blog

import com.demo.blog.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "posts")
data class Post(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    val blog: Blog,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val slug: String,

    val excerpt: String? = null,

    @Column(nullable = false, columnDefinition = "text")
    val content: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: PostStatus,

    @Column(name = "published_at")
    val publishedAt: LocalDateTime? = null,

    @Column(name = "featured_image")
    val featuredImage: String? = null,

    @Column(name = "view_count")
    var viewCount: Int = 0,

    @Column(name = "allow_comments")
    val allowComments: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany
    @JoinTable(
        name = "post_categories",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    val categories: MutableSet<Category> = mutableSetOf(),

    @ManyToMany
    @JoinTable(
        name = "post_tags",
        joinColumns = [JoinColumn(name = "post_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: MutableSet<Tag> = mutableSetOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL])
    val comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL])
    val revisions: MutableList<PostRevision> = mutableListOf()
)