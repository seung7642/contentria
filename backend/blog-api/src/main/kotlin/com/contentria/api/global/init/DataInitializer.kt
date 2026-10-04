package com.contentria.api.global.init

import com.contentria.api.user.domain.Role
import com.contentria.api.user.infrastructure.RoleJpaRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class DataInitializer(private val roleJpaRepository: RoleJpaRepository) {

    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun initRoles() {
        try {
            if (roleJpaRepository.findByName("ROLE_USER") == null) {
                roleJpaRepository.save(Role(name = "ROLE_USER", description = "일반 사용자"))
                logger.info { "ROLE_USER 역할이 생성되었습니다." }
            }

            if (roleJpaRepository.findByName("ROLE_ADMIN") == null) {
                roleJpaRepository.save(Role(name = "ROLE_ADMIN", description = "시스템 관리자"))
                logger.info { "ROLE_ADMIN 역할이 생성되었습니다." }
            }
        } catch (e: Exception) {
            logger.error(e) { "역할 초기화 중 오류 발생: ${e.message}" }
        }
    }
}