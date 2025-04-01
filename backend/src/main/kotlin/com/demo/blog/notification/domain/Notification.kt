package com.demo.blog.notification.domain

import com.demo.blog.user.domain.User
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "notifications")
class Notification(
    @Id
    @Column(columnDefinition = "UUID")
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    var recipientUser: User,

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    var type: NotificationType,

    @Column(columnDefinition = "UUID") // Matches DB type
    var relatedEntityId: UUID? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    var relatedEntityType: RelatedEntityType? = null,

    @Lob
    @Column(columnDefinition = "TEXT")
    var message: String? = null,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @Column(nullable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)