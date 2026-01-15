package com.contentria.api.auth.infrastructure.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class AuthUserDetails(
    val userId: UUID,
    val email: String,
    private val roles: List<String>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return roles.map { SimpleGrantedAuthority(it) }
    }

    /**
     * In JWT authentication, a password is not required, so we return null.
     */
    override fun getPassword(): String? = null

    override fun getUsername(): String? = email
}