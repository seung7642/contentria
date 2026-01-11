package com.contentria.api.user.domain

interface RoleRepository {

    fun save(role: Role): Role
    fun delete(role: Role)
    fun deleteAll(roles: List<Role>)
    fun findById(id: Long): Role?

    fun findByName(name: String): Role?
}