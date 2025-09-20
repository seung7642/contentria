package com.contentria.api.auth.domain

import com.contentria.api.user.domain.BaseEntity
import com.contentria.api.user.domain.User
import com.contentria.common.jpa.GeneratedUuidV7
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: User
) : BaseEntity()