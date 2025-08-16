package com.contentria.api.user.security

import com.contentria.api.user.repository.UserRepository
import com.contentria.api.user.security.CustomUserDetails
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmailWithRoles(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")
        return CustomUserDetails(user)
    }
}