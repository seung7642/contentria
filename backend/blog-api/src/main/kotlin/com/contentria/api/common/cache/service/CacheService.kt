package com.contentria.api.common.cache.service

interface CacheService {

    fun set(key: String, value: String, ttlSeconds: Long)

    fun get(key: String): String?

    fun delete(key: String): Boolean

    fun exists(key: String): Boolean

    fun setWithExpiry(key: String, value: String, ttlSeconds: Long): Boolean

    fun getTtl(key: String): Long
}