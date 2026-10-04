package com.contentria.api.global.config

import com.giffing.bucket4j.spring.boot.starter.config.cache.CacheManager
import com.giffing.bucket4j.spring.boot.starter.config.cache.SyncCacheResolver
import com.giffing.bucket4j.spring.boot.starter.config.cache.jcache.JCacheCacheResolver
import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Duration
import java.util.*
import javax.cache.Caching
import javax.cache.spi.CachingProvider


@Configuration
@EnableCaching
class CacheConfig {

//    @Bean
//    @Primary
//    fun bucket4jCacheResolver(): SyncCacheResolver {
//        val cachingProvider: CachingProvider = Caching.getCachingProvider()
//        val configuration: CaffeineConfiguration<Any, Any> = CaffeineConfiguration()
//        configuration.setExpireAfterWrite(OptionalLong.of(Duration.ofHours(1).toNanos()))
//        configuration.setMaximumSize(OptionalLong.of(1000000))
//        val cacheManager = cachingProvider.getCacheManager()
//        cacheManager.createCache("rateLimiting", configuration)
//        return JCacheCacheResolver(cacheManager)
//    }
}