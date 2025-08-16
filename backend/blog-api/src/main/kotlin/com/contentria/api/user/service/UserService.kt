package com.contentria.api.user.service

import com.contentria.api.user.domain.User
import com.contentria.api.user.security.GoogleUserInfo
import com.contentria.api.user.repository.RoleRepository
import com.contentria.api.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository
) {

    @Transactional
    fun upsertGoogleUser(googleUserInfo: GoogleUserInfo): User {
        return userRepository.findByEmail(googleUserInfo.email)?.let { existingUser ->
            var updated = false
            if (existingUser.realUsername != googleUserInfo.name) {
                existingUser.realUsername = googleUserInfo.name
                updated = true
            }
            if (existingUser.pictureUrl != googleUserInfo.picture) {
                existingUser.pictureUrl = googleUserInfo.picture
                updated = true
            }
            if (updated) {
                logger.info { "Updating existing user info for email [${existingUser.email}]" }
            } else{
                logger.debug { "Existing user found for email [${existingUser.email}]. No updates needed." }
            }
            existingUser
        } ?: run {
            logger.info { "Creating a new user for Google account email [${googleUserInfo.email}]" }
            val userRole = roleRepository.findByName("ROLE_USER")
                ?: throw IllegalArgumentException("Required role 'ROLE_USER' not found in the database. Please ensure it is initialized.")

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
                val savedUser = userRepository.save(newUser)
                logger.info { "Successfully saved new user with email [${newUser.email}]" }
                savedUser
            } catch (e: Exception) {
                logger.error(e) { "Failed to save new Google user with email [${googleUserInfo.email}]: ${e.message}" }
                throw RuntimeException("Failed to save new Google user with email [${googleUserInfo.email}]", e)
            }
        }
    }

    fun existsByEmail(email: String): Boolean {
//        return userRepository.existsByEmail(email)
        return false
    }
}