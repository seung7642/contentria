package com.demo.blog.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun webClientBuilder(): WebClient.Builder {
        val size = 16 * 1024 * 1024 // 16MB
        val strategies = ExchangeStrategies.builder()
            .codecs { codecs -> codecs.defaultCodecs().maxInMemorySize(size) }
            .build()

        return WebClient.builder()
            .exchangeStrategies(strategies)
    }
}