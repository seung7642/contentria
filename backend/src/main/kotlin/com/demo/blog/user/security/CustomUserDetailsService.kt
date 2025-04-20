package com.demo.blog.user.security

import com.demo.blog.user.repository.UserRepository
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
        // username 파라미터를 이메일로 간주
        val user = userRepository.findByUsernameWithRoles(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다: $username")
        return CustomUserDetails(user)
    }
}