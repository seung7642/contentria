package com.contentria.api.post.domain

import com.contentria.common.domain.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(
    name = "posts", uniqueConstraints = [
        UniqueConstraint(name = "uq_posts_blog_slug", columnNames = ["blog_id", "slug"])
    ]
)
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

    @Column(columnDefinition = "TEXT")
    var contentMarkdown: String,

    @Column(length = 500)
    var summary: String? = null,

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

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "blog_id", nullable = false)
//    var blog: Blog

    @Column(name = "blog_id", nullable = false, columnDefinition = "uuid")
    var blogId: UUID,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    var category: Category

    @Column(name = "category_id", columnDefinition = "uuid")
    var categoryId: UUID? = null,
) : BaseEntity() {

    companion object {
        fun create(
            slug: String,
            title: String,
            content: String,
            summary: String? = null,
            metaTitle: String? = null,
            metaDescription: String? = null,
            featuredImageUrl: String? = null,
            status: PostStatus = PostStatus.DRAFT,
            likeCount: Int = 0,
            viewCount: Int = 0,
            publishedAt: ZonedDateTime? = null,
            blogId: UUID,
            categoryId: UUID? = null
        ) : Post {
            return Post(
                slug = slug,
                title = title,
                contentMarkdown = content,
                summary = summary,
                metaTitle = metaTitle,
                metaDescription = metaDescription,
                featuredImageUrl = featuredImageUrl,
                status = status,
                likeCount = likeCount,
                viewCount = viewCount,
                publishedAt = publishedAt,
                blogId = blogId,
                categoryId = categoryId,
            )
        }
    }
}