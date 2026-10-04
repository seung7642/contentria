package com.contentria.worker.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Cloudflare Queues `http_pull` consumer settings. The API token needs Queues edit
 * (pull/ack) and is injected via env / K8s Secret — never committed.
 */
@ConfigurationProperties(prefix = "cloudflare.queue")
data class CloudflareProperties(
    val baseUrl: String = "https://api.cloudflare.com/client/v4",
    val accountId: String,
    val queueId: String,
    val apiToken: String,
    val batchSize: Int = 1,
    // Must exceed worst-case transcode time so a live job is never redelivered (30 min).
    val visibilityTimeoutMs: Long = 1_800_000,
)
