package com.contentria.api.video.domain

import com.contentria.common.global.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "videos")
class Video(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    var id: UUID? = null,

    @Column(name = "post_id", columnDefinition = "uuid")
    var postId: UUID? = null,

    @Column(name = "uploader_id", nullable = false, columnDefinition = "uuid")
    var uploaderId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    var status: VideoStatus = VideoStatus.PENDING,

    @Column(length = 255, nullable = false)
    var originalName: String,

    @Column(length = 500, nullable = false)
    var rawKey: String,

    var fileSize: Long? = null,

    @Column(length = 100)
    var contentType: String? = null,

    @Column(length = 500)
    var hlsPrefix: String? = null,

    @Column(length = 500)
    var masterKey: String? = null,

    @Column(length = 500)
    var posterKey: String? = null,

    var durationMs: Long? = null,

    var width: Int? = null,

    var height: Int? = null,

    @Column(columnDefinition = "TEXT")
    var errorMessage: String? = null,
) : BaseEntity() {

    fun isUploader(userId: UUID): Boolean {
        return this.uploaderId == userId
    }

    fun linkToPost(postId: UUID) {
        this.postId = postId
    }

    /**
     * Marks the video for cleanup (GC reclaims its R2 objects + row). The post link is
     * detached so the `uq_videos_post_active` partial index stays free regardless of the
     * flush order when a video is replaced.
     */
    fun markDeleted() {
        this.postId = null
        this.status = VideoStatus.DELETED
    }
}
