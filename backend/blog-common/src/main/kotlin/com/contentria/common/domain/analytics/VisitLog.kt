package com.contentria.common.domain.analytics

import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(
    name = "visit_logs",
    schema = "contentria"
//    indexes = [
//        Index(name = "idx_visit_logs_blot_date", columnList = "blog_id, visited_at")
//    ]
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