package com.contentria.api.user.domain

import com.contentria.common.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    var realUsername: String? = null,

    var username: String? = null,

    var password: String? = null,

    var pictureUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.UNVERIFIED,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var provider: AuthProvider = AuthProvider.EMAIL,

    var providerId: String? = null,

    @OneToMany(mappedBy = "user", cascade = [(CascadeType.ALL)], orphanRemoval = true)
    var userRoles: MutableSet<UserRole> = mutableSetOf(),

) : BaseEntity() {

    fun addRole(role: Role): UserRole {
        val userRole = UserRole(user = this, role = role)
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
                status = UserStatus.UNVERIFIED,
                provider = AuthProvider.EMAIL,
            )
        }

        fun createGoogleUser(email: String, realUsername: String, username: String, pictureUrl: String?, providerId: String): User {
            return User(
                email = email,
                realUsername = realUsername,
                username = username,
                pictureUrl = pictureUrl,
                status = UserStatus.ACTIVE,
                provider = AuthProvider.GOOGLE,
                providerId = providerId,
            )
        }
    }
}