package com.demo.blog

import com.demo.blog.user.domain.User
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "blogs")
data class Blog(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val name: String,

    @Column(unique = true)
    val subdomain: String? = null,

    val description: String? = null,

    val logo: String? = null,

    val theme: String = "default",

    @Column(name = "custom_css")
    val customCss: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL])
    val categories: MutableList<Category> = mutableListOf(),

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL])
    val posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL])
    val tags: MutableList<Tag> = mutableListOf(),

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL])
    val subscribers: MutableList<Subscriber> = mutableListOf()
)