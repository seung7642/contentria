package com.contentria.worker.transcode

import com.contentria.worker.config.TranscodeProperties
import com.contentria.worker.storage.WorkerStorageClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.time.Duration

private val log = KotlinLogging.logger {}

/**
 * Real transcoder: downloads the source from R2, validates it with ffprobe, encodes a
 * 720p/480p capped-CRF H.264/AAC fMP4 HLS ladder (no upscaling beyond the source) with a
 * poster, and uploads the outputs to `hls/{videoId}/`.
 *
 * ffmpeg/ffprobe are invoked as CLI subprocesses (bundled in the container image).
 */
@Component
class FfmpegTranscoder(
    private val properties: TranscodeProperties,
    private val processRunner: ProcessRunner,
    private val ffprobeService: FfprobeService,
    private val storageClient: WorkerStorageClient,
) : Transcoder {

    override fun transcode(job: TranscodeJob): TranscodeResult {
        val workDir = createWorkDir(job)
        try {
            val input = File(workDir, "input")
            storageClient.download(job.rawKey, input)

            val probe = ffprobeService.probe(input)
            if (probe.durationMs > properties.maxDurationSeconds * 1000) {
                throw PermanentTranscodeException(
                    "Video too long: ${probe.durationMs}ms > ${properties.maxDurationSeconds}s"
                )
            }

            val outDir = File(workDir, "out").apply { mkdirs() }
            val renditions = selectRenditions(probe.height)
            log.info {
                "Transcoding videoId=${job.videoId} source=${probe.width}x${probe.height} " +
                    "renditions=${renditions.joinToString { it.name }} audio=${probe.hasAudio}"
            }
            runHls(input, outDir, renditions, probe.hasAudio)

            val hlsPrefix = "hls/${job.videoId}/"
            uploadDirectory(outDir, hlsPrefix)
            val posterKey = generateAndUploadPoster(input, workDir, hlsPrefix, probe.durationMs, job)

            return TranscodeResult(
                hlsPrefix = hlsPrefix,
                masterKey = "${hlsPrefix}master.m3u8",
                posterKey = posterKey,
                durationMs = probe.durationMs,
                width = probe.width,
                height = probe.height,
            )
        } finally {
            workDir.deleteRecursively()
        }
    }

    private fun createWorkDir(job: TranscodeJob): File {
        val prefix = "video-${job.videoId}-"
        val root = properties.workDir?.let { File(it).apply { mkdirs() } }
        return if (root != null) {
            Files.createTempDirectory(root.toPath(), prefix).toFile()
        } else {
            Files.createTempDirectory(prefix).toFile()
        }
    }

    /** Fixed ladder filtered to renditions that do not upscale the source (no rung above source height). */
    private fun selectRenditions(sourceHeight: Int): List<Rendition> {
        val even = sourceHeight - (sourceHeight % 2)
        val applicable = LADDER.filter { it.height <= even }
        if (applicable.isNotEmpty()) return applicable
        // Source smaller than the lowest rung: a single rendition at source height (still no upscale).
        return listOf(LADDER.last().copy(name = "${even}p", height = even.coerceAtLeast(2)))
    }

    private fun runHls(input: File, outDir: File, renditions: List<Rendition>, hasAudio: Boolean) {
        val command = buildHlsCommand(input, outDir, renditions, hasAudio)
        val result = processRunner.run(command, Duration.ofMinutes(properties.processTimeoutMinutes))
        if (!result.isSuccess) {
            // After a successful ffprobe, a ffmpeg failure is treated as transient (retry → DLQ).
            throw RuntimeException("ffmpeg HLS encode failed (exit ${result.exitCode}): ${result.stderr.takeLast(800)}")
        }
    }

    private fun buildHlsCommand(
        input: File,
        outDir: File,
        renditions: List<Rendition>,
        hasAudio: Boolean,
    ): List<String> {
        val args = mutableListOf(properties.ffmpegPath, "-y", "-i", input.absolutePath)

        // filter_complex: split the source and scale each branch (-2 keeps aspect, even width).
        val splitLabels = renditions.indices.joinToString("") { "[v$it]" }
        val filter = StringBuilder("[0:v]split=${renditions.size}$splitLabels")
        renditions.forEachIndexed { i, r -> filter.append(";[v$i]scale=-2:${r.height}[vout$i]") }
        args += listOf("-filter_complex", filter.toString())

        // Per-rendition video encode (capped CRF).
        renditions.forEachIndexed { i, r ->
            args += listOf(
                "-map", "[vout$i]",
                "-c:v:$i", "libx264",
                "-preset", "medium",
                "-crf", r.crf.toString(),
                "-maxrate:v:$i", "${r.maxrateKbps}k",
                "-bufsize:v:$i", "${r.bufsizeKbps}k",
                "-g", "48", "-keyint_min", "48", "-sc_threshold", "0",
            )
        }

        // Per-rendition audio (re-encoded to AAC); skipped entirely for silent sources.
        if (hasAudio) {
            renditions.forEachIndexed { i, r ->
                args += listOf("-map", "a:0", "-c:a:$i", "aac", "-b:a:$i", "${r.audioKbps}k", "-ac", "2")
            }
        }

        val varStreamMap = renditions.indices.joinToString(" ") { if (hasAudio) "v:$it,a:$it" else "v:$it" }
        args += listOf(
            "-f", "hls",
            "-hls_time", properties.hlsSegmentSeconds.toString(),
            "-hls_playlist_type", "vod",
            "-hls_segment_type", "fmp4",
            "-hls_flags", "independent_segments",
            "-master_pl_name", "master.m3u8",
            "-hls_segment_filename", "${outDir.absolutePath}/stream_%v/segment_%03d.m4s",
            "-var_stream_map", varStreamMap,
            "${outDir.absolutePath}/stream_%v/playlist.m3u8",
        )
        return args
    }

    private fun generateAndUploadPoster(
        input: File,
        workDir: File,
        hlsPrefix: String,
        durationMs: Long,
        job: TranscodeJob,
    ): String? {
        val poster = File(workDir, "poster.jpg")
        val seekSeconds = if (durationMs < 2000) "0" else "1"
        val command = listOf(
            properties.ffmpegPath, "-y",
            "-ss", seekSeconds,
            "-i", input.absolutePath,
            "-frames:v", "1",
            "-vf", "scale=-2:720",
            poster.absolutePath,
        )
        return try {
            val result = processRunner.run(command, Duration.ofMinutes(2))
            if (!result.isSuccess || !poster.exists()) {
                log.warn { "Poster generation failed for ${job.videoId}; continuing without a poster" }
                return null
            }
            val key = "${hlsPrefix}poster.jpg"
            storageClient.upload(key, poster, "image/jpeg")
            key
        } catch (e: Exception) {
            log.warn(e) { "Poster generation failed for ${job.videoId}; continuing without a poster" }
            null
        }
    }

    private fun uploadDirectory(dir: File, keyPrefix: String) {
        dir.walkTopDown().filter { it.isFile }.forEach { file ->
            val relativePath = file.relativeTo(dir).path.replace(File.separatorChar, '/')
            storageClient.upload(keyPrefix + relativePath, file, contentTypeFor(file.name))
        }
    }

    private fun contentTypeFor(fileName: String): String = when {
        fileName.endsWith(".m3u8") -> "application/vnd.apple.mpegurl"
        fileName.endsWith(".m4s") -> "video/mp4"
        fileName.endsWith(".mp4") -> "video/mp4"
        fileName.endsWith(".jpg") -> "image/jpeg"
        else -> "application/octet-stream"
    }

    companion object {
        // Capped-CRF ladder, ordered high → low. See backend/VIDEO-PLAN.md §2.2.
        private val LADDER = listOf(
            Rendition(name = "720p", height = 720, crf = 21, maxrateKbps = 3000, bufsizeKbps = 6000, audioKbps = 128),
            Rendition(name = "480p", height = 480, crf = 23, maxrateKbps = 1200, bufsizeKbps = 2400, audioKbps = 96),
        )
    }
}

data class Rendition(
    val name: String,
    val height: Int,
    val crf: Int,
    val maxrateKbps: Int,
    val bufsizeKbps: Int,
    val audioKbps: Int,
)
