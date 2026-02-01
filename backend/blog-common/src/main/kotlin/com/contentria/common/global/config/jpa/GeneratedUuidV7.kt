package com.contentria.common.global.config.jpa

import org.hibernate.annotations.IdGeneratorType

@IdGeneratorType(UuidV7Generator::class)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
annotation class GeneratedUuidV7
