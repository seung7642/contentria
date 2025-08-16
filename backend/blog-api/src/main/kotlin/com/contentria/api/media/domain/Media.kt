package com.contentria.api.media.domain

import com.contentria.api.post.domain.Post
import com.contentria.api.user.domain.User
import com.contentria.api.media.domain.StorageType
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "media")
class Media(
    @Id
    @Column(columnDefinition = "UUID")
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    var post: Post? = null,

    @Column(length = 255)
    var fileName: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var filePath: String,

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    var fileUrl: String,

    @Column(length = 100)
    var mimeType: String,

    var fileSizeBytes: Long,

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    var storageType: StorageType = StorageType.LOCAL,

    @Column(nullable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)