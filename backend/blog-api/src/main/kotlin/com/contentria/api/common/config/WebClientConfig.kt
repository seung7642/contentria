package com.contentria.api.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(10)) // 예시: 응답 타임아웃 10초

        return builder
            // .baseUrl("https://default-base-url.com") // 기본 URL 설정 (reCAPTCHA는 전체 URL 사용)
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .codecs { configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) } // 버퍼 크기 설정 (선택)
            .build()
    }
}