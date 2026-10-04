package com.contentria.core.shared.exception

/**
 * Root of every failure raised by a business rule inside [com.contentria.core].
 *
 * Inbound adapters translate these into their own protocol — an HTTP status in `app/api`,
 * an exit code in `app/batch`. The domain never knows how a failure is reported.
 */
abstract class DomainException(message: String) : RuntimeException(message)
