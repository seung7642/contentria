package com.contentria.common.cache

import org.springframework.stereotype.Service

@Service
class CacheServiceImpl(
) : CacheService {

    override fun set(key: String, value: String, ttlSeconds: Long) {
        TODO("Not yet implemented")
    }

    override fun get(key: String): String? {
        TODO("Not yet implemented")
    }

    override fun delete(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun exists(key: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun setWithExpiry(key: String, value: String, ttlSeconds: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun getTtl(key: String): Long {
        TODO("Not yet implemented")
    }
}