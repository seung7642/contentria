package com.demo.blog.service

import com.demo.blog.dto.GoogleUserInfo
import com.demo.blog.entity.User
import com.demo.blog.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserService(private val userRepository: UserRepository) : UserDetailsService {

    @Transactional
    fun createOrUpdateGoogleUser(googleUserInfo: GoogleUserInfo): User {
        val userOptional = userRepository.findByEmail(googleUserInfo.email)

        return if (userOptional.isPresent) {
            val existingUser = userOptional.get()
            // 기존 사용자가 있는 경우, 필요시 정보 업데이트
            existingUser
        } else {
            // 새 사용자 생성
            val newUser = User(
                email = googleUserInfo.email,
                name = googleUserInfo.name,
                pictureUrl = googleUserInfo.picture,
                provider = "google",
                providerId = googleUserInfo.id
            )
            userRepository.save(newUser)
        }
    }

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            .orElseThrow { UsernameNotFoundException("User not found with email: $username") }

        val authorities = user.roles.map { SimpleGrantedAuthority(it) }

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password("") // OAuth 사용자는 비밀번호가 없음
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build()
    }
}