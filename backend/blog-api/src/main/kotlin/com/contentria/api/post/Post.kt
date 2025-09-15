package com.contentria.api.post

import com.contentria.api.blog.domain.Blog
import com.contentria.api.category.Category
import com.contentria.api.comment.Comment
import com.contentria.api.media.Media
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "posts", uniqueConstraints = [
    UniqueConstraint(name = "uq_posts_blog_slug", columnNames = ["blog_id", "slug"])
])
class Post(
    @Id
    @Column(columnDefinition = "UUID")
    var id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    var blog: Blog,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    var category: Category,

    @Column(length = 255, nullable = false)
    var slug: String,

    @Column(length = 255, nullable = false)
    var title: String,

    // Use @Lob for TEXT columns
    @Lob
    @Column(columnDefinition = "TEXT")
    var contentMarkdown: String,

    @Lob
    @Column(columnDefinition = "TEXT")
    var contentHtml: String,

    @Column(length = 255)
    var metaTitle: String,

    @Column(length = 500)
    var metaDescription: String? = null,

    @Column(columnDefinition = "TEXT")
    var featuredImageUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    var status: PostStatus = PostStatus.DRAFT,

    var publishedAt: ZonedDateTime? = null,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf(),

    @OneToMany(mappedBy = "post", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var mediaList: MutableList<Media> = mutableListOf(),

    @Column(nullable = false)
    var createdAt: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now()
)