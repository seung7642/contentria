package com.contentria.common.config

import com.contentria.common.global.config.MailProperties
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for integration tests within the 'common' module.
 * This class sets up a minimal Spring application context for testing purposes.
 *
 * - @Configuration: Indicates that a class supplies Spring Bean definitions for tests.
 * - @EnableAutoConfiguration: Enables Spring Boot's autoconfiguration mechanism.
 * - @ComponentScan: Scans for Spring components in specified packages.
 * - @EnableConfigurationProperties: Enables the injection of @ConfigurationProperties beans.
 */
@Configuration
@EnableAutoConfiguration(
    exclude = [
        DataSourceAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
        HibernateJpaAutoConfiguration::class
    ]
)
@ComponentScan(basePackages = ["com.contentria.common.infrastructure.email", "com.contentria.common.config"])
@EnableConfigurationProperties(MailProperties::class)
class CommonTestConfiguration {
}