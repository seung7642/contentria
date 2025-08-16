package com.demo.com.contentria.api.common.ratelimit.config

import io.github.bucket4j.distributed.proxy.ProxyManager
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.codec.StringCodec
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
class RateLimitConfig(
    private val connectionFactory: LettuceConnectionFactory
) {

//    @Bean
//    fun proxyManager(): ProxyManager<String> {
//        val redisClient = RedisClient.create(connectionFactory.clientConfiguration, connectionFactory.uri)
//        val connection: StatefulRedisConnection<String, ByteArray> = redisClient.connect(StringCodec.UTF8)
//
//
//    }
}