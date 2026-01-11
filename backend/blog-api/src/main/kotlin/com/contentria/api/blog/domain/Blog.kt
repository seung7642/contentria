package com.contentria.api.blog.domain

import com.contentria.api.user.domain.BaseEntity
import com.contentria.api.user.domain.User
import com.contentria.common.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "blogs")
class Blog(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    var id: UUID? = null,

    @Column(length = 30, unique = true, nullable = false)
    var slug: String,

    @Column(length = 255, nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    var user: User,

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    var userId: UUID
) : BaseEntity() {

    fun isOwner(userId: UUID): Boolean {
        return this.userId == userId
    }
}