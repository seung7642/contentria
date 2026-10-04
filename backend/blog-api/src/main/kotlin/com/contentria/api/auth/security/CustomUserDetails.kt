package com.contentria.api.auth.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

class CustomUserDetails(
    val userId: UUID,
    val email: String,
    val passwordHash: String,
    val roles: List<String>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { SimpleGrantedAuthority(it) }
    }

    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = email
}