package com.contentria.worker.transcode

import com.contentria.worker.config.TranscodeProperties
import org.springframework.stereotype.Component
import java.io.File
import java.time.Duration
import tools.jackson.databind.ObjectMapper

/** Probes a source file with ffprobe and validates it is a usable video. */
@Component
class FfprobeService(
    private val properties: TranscodeProperties,
    private val processRunner: ProcessRunner,
    private val objectMapper: ObjectMapper,
) {

    fun probe(input: File): ProbeResult {
        val command = listOf(
            properties.ffprobePath,
            "-v", "error",
            "-print_format", "json",
            "-show_format",
            "-show_streams",
            input.absolutePath,
        )
        val result = processRunner.run(command, Duration.ofMinutes(1))
        if (!result.isSuccess) {
            throw PermanentTranscodeException("ffprobe failed: ${result.stderr.take(500)}")
        }

        val root = objectMapper.readTree(result.stdout)
        val streams = root.path("streams")
        val video = streams.firstOrNull { it.path("codec_type").asString() == "video" }
            ?: throw PermanentTranscodeException("No video stream found")

        val width = video.path("width").asInt(0)
        val height = video.path("height").asInt(0)
        if (width <= 0 || height <= 0) {
            throw PermanentTranscodeException("Invalid video dimensions: ${width}x$height")
        }

        val hasAudio = streams.any { it.path("codec_type").asString() == "audio" }
        val durationSeconds = root.path("format").path("duration").asString("0").toDoubleOrNull() ?: 0.0

        return ProbeResult(
            width = width,
            height = height,
            durationMs = (durationSeconds * 1000).toLong(),
            hasAudio = hasAudio,
        )
    }
}

data class ProbeResult(
    val width: Int,
    val height: Int,
    val durationMs: Long,
    val hasAudio: Boolean,
)
