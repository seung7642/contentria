package com.contentria.api.auth.domain

import com.contentria.common.domain.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "refresh_tokens")
class RefreshToken(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 512)
    var token: String,

    @Column(nullable = false)
    var expiryDate: Instant,

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
//    var user: User

    @Column(name = "user_id", nullable = false)
    val userId: UUID
) : BaseEntity()