package com.demo.blog

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "subscribers")
data class Subscriber(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    val blog: Blog,

    @Column(nullable = false)
    val email: String,

    val name: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: SubscriberStatus,

    @Column(name = "confirmation_token")
    val confirmationToken: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)