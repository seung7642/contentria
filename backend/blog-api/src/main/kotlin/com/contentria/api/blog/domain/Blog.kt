package com.contentria.api.blog.domain

import com.contentria.api.category.Category
import com.contentria.api.post.Post
import com.contentria.api.subscription.Subscription
import com.contentria.api.user.domain.User
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
@Table(name = "blogs")
class Blog(
    @Id
    @Column(columnDefinition = "UUID")
    var id: String = UUID.randomUUID().toString(),

    @Column(length = 30, unique = true, nullable = false)
    var slug: String,

    @Column(length = 255, nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var posts: MutableList<Post> = mutableListOf(),

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var categories: MutableList<Category> = mutableListOf(),

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var subscriptions: MutableList<Subscription> = mutableListOf(),
)