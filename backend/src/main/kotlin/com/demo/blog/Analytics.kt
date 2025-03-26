package com.demo.blog

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "analytics")
data class Analytics(
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "uuid", updatable = false)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    val blog: Blog,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    val post: Post? = null,

    @Column(name = "page_path")
    val pagePath: String? = null,

    @Column(nullable = false)
    var views: Int = 0,

    @Column(name = "unique_visitors", nullable = false)
    var uniqueVisitors: Int = 0,

    @Column(name = "avg_time_on_page")
    var avgTimeOnPage: Float? = null,

    @Column(nullable = false)
    val date: LocalDate,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)