package com.demo.blog.user.domain

import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "user_roles", uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role_id"])])
class UserRole(
    @Id
    @Column(length = 36)
    val id: String = UUID.randomUUID().toString(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    val role: Role,

    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    val createdBy: String? = null,

    val updatedAt: ZonedDateTime = ZonedDateTime.now(),

    val updatedBy: String? = null,
)