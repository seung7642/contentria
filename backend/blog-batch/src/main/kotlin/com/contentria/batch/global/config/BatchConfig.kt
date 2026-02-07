package com.contentria.batch.global.config

import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.Isolation

//@Configuration
//class BatchConfig : DefaultBatchConfiguration() {
//
//    override fun getIsolationLevelForCreate(): Isolation? {
//        return Isolation.REPEATABLE_READ
//    }
//}