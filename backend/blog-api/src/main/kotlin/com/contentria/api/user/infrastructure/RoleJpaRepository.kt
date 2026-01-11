package com.contentria.api.user.infrastructure

import com.contentria.api.user.domain.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleJpaRepository : JpaRepository<Role, Long> {

    fun findByName(name: String): Role?
}