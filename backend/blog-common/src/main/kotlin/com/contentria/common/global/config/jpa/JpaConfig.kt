package com.contentria.common.global.config.jpa

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.time.ZonedDateTime
import java.util.*

@EntityScan(basePackages = ["com.contentria"])
@EnableJpaRepositories(basePackages = ["com.contentria"])
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@Configuration
class JpaConfig {

    @Bean
    fun auditingDateTimeProvider(): DateTimeProvider {
        return DateTimeProvider { Optional.of(ZonedDateTime.now()) }
    }
}