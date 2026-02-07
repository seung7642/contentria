package com.contentria.common.domain.analytics

import com.contentria.common.domain.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(
    name = "daily_statistics",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uq_daily_stats_blog_post_date",
            columnNames = ["blog_id", "post_id", "stat_date"]
        )
    ],
    indexes = [
        Index(name = "idx_daily_stats_date", columnList = "blog_id, stat_date")
    ],
    schema = "contentria"
)
class DailyStatistics(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false)
    val blogId: UUID,

    @Column(nullable = true)
    val postId: UUID?,

    @Column(nullable = false)
    val statDate: LocalDate,

    @Column(nullable = false)
    var visitCount: Long = 0L,

    @Column(nullable = false)
    var viewCount: Long = 0L,
) : BaseEntity() {

    fun incrementCounts(visits: Long, views: Long) {
        this.visitCount += visits
        this.viewCount += views
    }

    companion object {
        fun create(
            blogId: UUID,
            postId: UUID?,
            statDate: LocalDate,
            visitCount: Long = 0,
            viewCount: Long = 0
        ) : DailyStatistics {
            return DailyStatistics(
                blogId = blogId,
                postId = postId,
                statDate = statDate,
                visitCount = visitCount,
                viewCount = viewCount
            )
        }
    }
}