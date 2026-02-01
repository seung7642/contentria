package com.contentria.common.global.config.jpa

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.ZonedDateTime
import java.util.*

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class JpaConfig {

    @Bean
    fun auditingDateTimeProvider(): DateTimeProvider {
        return DateTimeProvider { Optional.of(ZonedDateTime.now()) }
    }
}