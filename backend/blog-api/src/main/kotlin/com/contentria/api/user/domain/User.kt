package com.contentria.api.user.domain

import com.contentria.common.domain.model.BaseEntity
import com.contentria.common.global.config.jpa.GeneratedUuidV7
import jakarta.persistence.*
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

    @Column(nullable = false, unique = true, length = 50)
    var nickname: String,

    var pictureUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: UserStatus = UserStatus.UNVERIFIED,

    @OneToMany(mappedBy = "user", cascade = [(CascadeType.ALL)], orphanRemoval = true)
    var userRoles: MutableSet<UserRole> = mutableSetOf(),

) : BaseEntity() {

    fun addRole(role: Role): UserRole {
        val userRole = UserRole(user = this, role = role)
        userRoles.add(userRole)
        return userRole
    }

    companion object {
        fun createEmailUser(email: String, username: String, nickname: String): User {
            return User(
                email = email,
                username = username,
                nickname = nickname,
                status = UserStatus.UNVERIFIED,
            )
        }

        fun createSocialUser(email: String, realUsername: String, username: String, nickname: String, pictureUrl: String?): User {
            return User(
                email = email,
                realUsername = realUsername,
                username = username,
                nickname = nickname,
                pictureUrl = pictureUrl,
                status = UserStatus.ACTIVE,
            )
        }
    }
}