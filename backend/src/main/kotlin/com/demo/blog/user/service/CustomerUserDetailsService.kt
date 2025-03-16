package com.demo.blog.user.service

import com.demo.blog.user.domain.User
import com.demo.blog.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        // username 파라미터를 이메일로 간주
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")
        return createUserDetails(user)
    }

    private fun createUserDetails(user: User): UserDetails {
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.username)
            .password("")
            .authorities(user.userRoles.map { it.role }.toList())
            .build()
    }
}