package com.contentria.worker.queue

import com.contentria.worker.config.CloudflareProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import tools.jackson.databind.ObjectMapper

private val log = KotlinLogging.logger {}

/**
 * Thin client over the Cloudflare Queues `http_pull` REST API.
 *
 *  - `pull`  → POST .../messages/pull  (batch + visibility timeout)
 *  - `ack`   → POST .../messages/ack   (acknowledge processed messages by lease id)
 *
 * Not acking a message lets the queue redeliver it after the visibility timeout; after
 * the configured max retries it lands in the dead-letter queue.
 */
@Component
class CloudflareQueueClient(
    private val props: CloudflareProperties,
    private val objectMapper: ObjectMapper,
) {

    // Timeouts are mandatory here: the poll loop runs on a single scheduler thread, so a
    // hung connection without a timeout would stall the whole worker permanently.
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(CONNECT_TIMEOUT)
        .build()

    private fun queueUrl(): String =
        "${props.baseUrl}/accounts/${props.accountId}/queues/${props.queueId}"

    fun pull(): List<QueueMessage> {
        val body = objectMapper.writeValueAsString(
            mapOf(
                "visibility_timeout_ms" to props.visibilityTimeoutMs,
                "batch_size" to props.batchSize,
            )
        )
        val response = send("${queueUrl()}/messages/pull", body)

        val messages = objectMapper.readTree(response).path("result").path("messages")
        return messages.mapNotNull { node ->
            val leaseId = node.path("lease_id").asString(null) ?: return@mapNotNull null
            val event = node.path("body")
            QueueMessage(
                leaseId = leaseId,
                objectKey = event.path("object").path("key").asString(""),
                action = event.path("action").asString(""),
            )
        }
    }

    fun ack(leaseIds: List<String>) {
        if (leaseIds.isEmpty()) return
        val body = objectMapper.writeValueAsString(
            mapOf("acks" to leaseIds.map { mapOf("lease_id" to it) })
        )
        send("${queueUrl()}/messages/ack", body)
        log.debug { "Acked ${leaseIds.size} message(s)" }
    }

    private fun send(url: String, jsonBody: String): String {
        val request = HttpRequest.newBuilder(URI.create(url))
            .timeout(REQUEST_TIMEOUT)
            .header("Authorization", "Bearer ${props.apiToken}")
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw CloudflareQueueException("Cloudflare Queue request failed: $url -> ${response.statusCode()} ${response.body()}")
        }
        return response.body()
    }

    companion object {
        private val CONNECT_TIMEOUT: Duration = Duration.ofSeconds(10)

        // Cloudflare's pull returns immediately (it is not a long-poll), so 30s is generous.
        // A timeout surfaces as an exception -> caught by the poll loop -> retried next tick;
        // unacked messages are redelivered after the queue's visibility timeout.
        private val REQUEST_TIMEOUT: Duration = Duration.ofSeconds(30)
    }
}

class CloudflareQueueException(message: String) : RuntimeException(message)
