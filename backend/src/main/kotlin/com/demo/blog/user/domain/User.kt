package com.demo.blog.user.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @Column(length = 36)
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false, unique = true)
    val email: String,

    val realUsername: String? = null,

    val username: String? = null,

    val password: String? = null,

    val pictureUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider = AuthProvider.EMAIL,

    val providerId: String? = null,

    @OneToMany(mappedBy = "user", cascade = [(CascadeType.ALL)], orphanRemoval = true)
    val userRoles: MutableSet<UserRole> = mutableSetOf(),

    val createdAt: ZonedDateTime = ZonedDateTime.now(),

    val updatedAt: ZonedDateTime = ZonedDateTime.now()
) {
    fun addRole(role: Role, createdBy: String? = null): UserRole {
        val userRole = UserRole(user = this, role = role, createdBy = createdBy)
        userRoles.add(userRole)
        return userRole
    }

    fun hasRole(roleName: String): Boolean {
        val roleNameWithPrefix = if (roleName.startsWith("ROLE_")) roleName else "ROLE_$roleName"
        return userRoles.any { it.role.authority == roleNameWithPrefix.uppercase() }
    }

    fun isAdmin(): Boolean = hasRole("ADMIN")

    fun getAuthorities(): List<GrantedAuthority> = userRoles.map { it.role }

    // 팩토리 메소드로 생성 방식 표준화
    companion object {
        fun createEmailUser(email: String, username: String, password: String): User {
            return User(
                email = email,
                username = username,
                password = password,
                provider = AuthProvider.EMAIL,
            )
        }

        fun createGoogleUser(email: String, realUsername: String, username: String, pictureUrl: String?, providerId: String): User {
            return User(
                email = email,
                realUsername = realUsername,
                username = username,
                pictureUrl = pictureUrl,
                provider = AuthProvider.GOOGLE,
                providerId = providerId,
            )
        }
    }
}