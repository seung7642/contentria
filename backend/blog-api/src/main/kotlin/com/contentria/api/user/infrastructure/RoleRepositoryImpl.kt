package com.contentria.api.user.infrastructure

import com.contentria.api.user.domain.Role
import com.contentria.api.user.domain.RoleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class RoleRepositoryImpl(
    private val roleJpaRepository: RoleJpaRepository
) : RoleRepository {

    override fun save(role: Role): Role {
        return roleJpaRepository.save(role)
    }

    override fun delete(role: Role) {
        roleJpaRepository.delete(role)
    }

    override fun deleteAll(roles: List<Role>) {
        roleJpaRepository.deleteAll(roles)
    }

    override fun findById(id: Long): Role? {
        return roleJpaRepository.findByIdOrNull(id)
    }

    override fun findByName(name: String): Role? {
        return roleJpaRepository.findByName(name)
    }
}