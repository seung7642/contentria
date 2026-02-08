package com.contentria.batch.global.config

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    @field:Valid val batch: AppBatchProperties
)

@Validated
data class AppBatchProperties(
    @field:NotBlank val jobName: String
)