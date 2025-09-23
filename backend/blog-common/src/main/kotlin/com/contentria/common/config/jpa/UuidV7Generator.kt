package com.contentria.common.config.jpa

import com.github.f4b6a3.uuid.UuidCreator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.generator.BeforeExecutionGenerator
import org.hibernate.generator.EventType
import org.hibernate.generator.EventTypeSets.INSERT_ONLY
import java.util.*

class UuidV7Generator : BeforeExecutionGenerator {

    override fun generate(
        session: SharedSessionContractImplementor?,
        owner: Any?,
        currentValue: Any?,
        eventType: EventType?
    ): Any? {
        return UuidCreator.getTimeOrderedEpoch()
    }

    override fun getEventTypes(): EnumSet<EventType?>? {
        return INSERT_ONLY
    }
}