package com.contentria.api.media.domain

import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "media")
class Media(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    var id: UUID? = null,

    @Column(name = "post_id", columnDefinition = "uuid")
    var postId: UUID? = null,

    @Column(name = "uploader_id", nullable = false, columnDefinition = "uuid")
    var uploaderId: UUID,

    @Column(length = 255, nullable = false)
    var originalName: String,

    @Column(length = 500, nullable = false)
    var storedKey: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    var publicUrl: String,

    @Column(length = 100, nullable = false)
    var contentType: String,

    @Column(nullable = false)
    var fileSize: Long,

    @Column(nullable = false, updatable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now()
) {

    fun isUploader(userId: UUID): Boolean {
        return this.uploaderId == userId
    }

    fun linkToPost(postId: UUID) {
        this.postId = postId
    }
}
