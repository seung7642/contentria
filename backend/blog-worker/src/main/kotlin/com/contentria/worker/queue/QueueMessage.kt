package com.contentria.worker.queue

/**
 * A single message pulled from the Cloudflare Queue, flattened from the R2
 * event-notification payload to the fields the worker needs.
 */
data class QueueMessage(
    val leaseId: String,
    val objectKey: String,
    val action: String,
)
