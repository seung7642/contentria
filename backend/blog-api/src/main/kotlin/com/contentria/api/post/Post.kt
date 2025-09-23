package com.contentria.api.post

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.Category
import com.contentria.api.user.domain.BaseEntity
import com.contentria.common.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "posts", uniqueConstraints = [
    UniqueConstraint(name = "uq_posts_blog_slug", columnNames = ["blog_id", "slug"])
])
class Post(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    var id: UUID? = null,

    @Column(length = 255, nullable = false)
    var slug: String,

    @Column(length = 255, nullable = false)
    var title: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    var contentMarkdown: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    var contentHtml: String,

    @Column(length = 255)
    var metaTitle: String? = null,

    @Column(length = 500)
    var metaDescription: String? = null,

    @Column(columnDefinition = "TEXT")
    var featuredImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    var status: PostStatus = PostStatus.DRAFT,

    var likeCount: Int = 0,

    var viewCount: Int = 0,

    var publishedAt: ZonedDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    var blog: Blog,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = null,
) : BaseEntity()