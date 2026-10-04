package com.contentria.api.blog.domain

import com.contentria.common.global.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
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

    /** 블로그 공개 정보(제목/설명) 수정 — slug는 URL 정체성이라 변경 대상이 아니다 */
    fun updateSettings(title: String, description: String?) {
        this.title = title
        this.description = description
    }
}