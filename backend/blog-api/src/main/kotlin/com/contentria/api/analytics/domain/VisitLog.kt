package com.contentria.api.analytics.domain

import com.contentria.common.config.jpa.GeneratedUuidV7
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(
    name = "visit_logs",
    indexes = [
        Index(name = "idx_visit_logs_blot_date", columnList = "blog_id, visited_at")
    ]
)
class VisitLog(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false)
    val blogId: UUID,

    @Column(nullable = true)
    val postId: UUID?,

    @Column(length = 45)
    val visitorIp: String?,

    @Column(columnDefinition = "TEXT")
    val userAgent: String?,

    @Column(columnDefinition = "TEXT")
    val refererUrl: String?,

    @Column(nullable = false, updatable = false)
    val visitedAt: ZonedDateTime = ZonedDateTime.now()
) {
    companion object {
        fun create(
            blogId: UUID,
            postId: UUID?,
            visitorIp: String?,
            userAgent: String?,
            refererUrl: String?
        ): VisitLog {
            return VisitLog(
                blogId = blogId,
                postId = postId,
                visitorIp = visitorIp,
                userAgent = userAgent,
                refererUrl = refererUrl
            )
        }
    }
}