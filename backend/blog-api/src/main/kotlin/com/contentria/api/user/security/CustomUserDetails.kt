package com.contentria.api.user.security

import com.contentria.api.user.domain.AuthProvider
import com.contentria.api.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

data class CustomUserDetails(
    private val user: User
) : UserDetails {

    val userId: UUID? get() = user.id
    val email: String get() = user.email
    val profileImageUrl: String? get() = user.pictureUrl
    val displayName: String? get() = user.username
    val realUsername: String? get() = user.realUsername
    val provider: AuthProvider get() = user.provider

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return user.getAuthorities().toSet()
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}