package com.contentria.api.user.domain

import com.contentria.common.jpa.GeneratedUuidV7
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import java.util.*

@Entity
@Table(name = "roles")
class Role(
    @Id
    @GeneratedValue
    @GeneratedUuidV7
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false, unique = true, length = 50)
    val name: String,

    @Column(length = 255)
    val description: String? = null,

) : GrantedAuthority, BaseEntity() {

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