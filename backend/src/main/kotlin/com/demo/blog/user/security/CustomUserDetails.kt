package com.demo.blog.user.security

import com.demo.blog.user.domain.AuthProvider
import com.demo.blog.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class CustomUserDetails(
    private val user: User
) : UserDetails {

    fun getUserId(): String = user.id
    fun getEmail(): String = user.email
    fun getProfileImageUrl(): String? = user.pictureUrl
    fun getDisplayName(): String? = user.username
    fun getRealUsername(): String? = user.realUsername
    fun getProvider(): AuthProvider = user.provider

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return user.getAuthorities().toMutableSet()
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