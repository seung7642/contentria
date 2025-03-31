package com.demo.blog.user.service

import com.demo.blog.user.domain.User
import com.demo.blog.auth.dto.GoogleUserInfo
import com.demo.blog.user.repository.RoleRepository
import com.demo.blog.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createOrUpdateGoogleUser(googleUserInfo: GoogleUserInfo): User {
        return userRepository.findByEmail(googleUserInfo.email)?.let { existingUser ->
            // 기존 사용자가 있는 경우, 필요시 정보 업데이트
            existingUser
        } ?: run {
            val userRole = roleRepository.findByName("ROLE_USER")
                ?: throw IllegalArgumentException("ROLE_USER 역할이 데이터베이스에 존재하지 않습니다.")

            // 새 사용자 생성
            val newUser = User.createGoogleUser(
                email = googleUserInfo.email,
                realUsername = googleUserInfo.name,
                username = googleUserInfo.name,
                pictureUrl = googleUserInfo.picture,
                providerId = googleUserInfo.id
            )

            newUser.userRoles.clear()
            newUser.addRole(userRole)

            try {
                userRepository.save(newUser)
            } catch (e: Exception) {
                logger.error(e) { "failed UserService createOrUpdateGoogleUser: ${e.message}" }
                throw RuntimeException("failed UserService createOrUpdateGoogleUser", e)
            }
        }
    }
}