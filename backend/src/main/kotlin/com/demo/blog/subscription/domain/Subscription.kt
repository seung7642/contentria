package com.demo.blog.subscription.domain

import com.demo.blog.blog.domain.Blog
import com.demo.blog.user.domain.User
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "subscriptions", uniqueConstraints = [
    UniqueConstraint(name = "uq_subscriptions_user_blog", columnNames = ["user_id", "blog_id"])
])
class Subscription(
    @Id
    @Column(columnDefinition = "UUID")
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    var blog: Blog,

    @Column(nullable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)