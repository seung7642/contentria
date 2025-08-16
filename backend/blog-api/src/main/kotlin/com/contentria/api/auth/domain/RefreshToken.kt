package com.contentria.api.auth.domain

import com.contentria.api.user.domain.User
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "refresh_token")
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    var user: User,

    @Column(nullable = false, unique = true, length = 512)
    var token: String,

    @Column(nullable = false)
    var expiryDate: Instant
)