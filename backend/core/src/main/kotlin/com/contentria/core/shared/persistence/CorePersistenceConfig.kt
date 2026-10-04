package com.contentria.core.shared.persistence

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

/**
 * Makes `core`'s entities visible to an application module.
 *
 * Every application module has its own component scan root, so nothing under
 * `com.contentria.core` is found by default. Declaring the scan here rather than on each
 * application class keeps knowledge of `core`'s package layout inside `core`, and keeps the
 * JPA dependency off the application modules' compile classpath: they only need
 * `@Import(CorePersistenceConfig::class)`.
 */
@Configuration(proxyBeanMethods = false)
@EntityScan("com.contentria.core")
class CorePersistenceConfig
