package com.demo.blog.user.service

import com.demo.blog.user.domain.User
import com.demo.blog.auth.dto.GoogleUserInfo
import com.demo.blog.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {

    @Transactional
    fun createOrUpdateGoogleUser(googleUserInfo: GoogleUserInfo): User {
        return userRepository.findByEmail(googleUserInfo.email)?.let { existingUser ->
            // 기존 사용자가 있는 경우, 필요시 정보 업데이트
            existingUser
        } ?: run {
            // 새 사용자 생성
            val newUser = User.createGoogleUser(
                email = googleUserInfo.email,
                username = googleUserInfo.name,
                pictureUrl = googleUserInfo.picture,
                providerId = googleUserInfo.id
            )
            userRepository.save(newUser)
        }
    }
}