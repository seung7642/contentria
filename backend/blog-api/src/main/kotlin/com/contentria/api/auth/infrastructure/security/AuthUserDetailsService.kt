package com.contentria.api.auth.infrastructure.security

import com.contentria.api.user.application.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthUserDetailsService(
    private val userService: UserService
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        try {
            val user = userService.findActiveUserByEmail(email)

            return AuthUserDetails(
                userId = user.id!!,
                email = user.email,
                roles = user.userRoles.map { it.role.name }
            )
        } catch (e: Exception) {
            // Instead of using the project's exception policy `ContentriaException`,
            // we throw Spring Security's UsernameNotFoundException.
            throw UsernameNotFoundException("User not found or inactive: $email", e)
        }
    }
}