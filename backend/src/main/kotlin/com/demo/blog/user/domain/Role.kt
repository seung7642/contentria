package com.demo.blog.user.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    private val name: String,

    @Column(length = 255)
    val description: String? = null,
) : GrantedAuthority {

    override fun getAuthority(): String = name

    companion object {
        const val USER = "ROLE_USER"
        const val ADMIN = "ROLE_ADMIN"

        fun user() = Role(name = USER, description = "일반 사용자")
        fun admin() = Role(name = ADMIN, description = "시스템 관리자")

        fun of(roleName: String, description: String? = null): Role {
            val formattedName = if (roleName.startsWith("ROLE_")) roleName else "ROLE_$roleName"
            return Role(name = formattedName.uppercase(), description = description)
        }
    }
}