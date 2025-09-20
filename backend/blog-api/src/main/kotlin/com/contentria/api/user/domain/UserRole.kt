package com.contentria.api.user.domain

import jakarta.persistence.*

@Entity
@Table(name = "user_roles", uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role_id"])])
@IdClass(UserRoleId::class)
class UserRole(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    val role: Role,

) : BaseEntity()