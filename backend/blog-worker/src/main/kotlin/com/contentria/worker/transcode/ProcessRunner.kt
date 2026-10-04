package com.contentria.worker.transcode

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.io.File
import java.time.Duration
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

/**
 * Runs an external process (ffmpeg/ffprobe) and captures its output. Output is redirected
 * to temp files to avoid pipe-buffer deadlocks on chatty tools like ffmpeg.
 */
@Component
class ProcessRunner {

    fun run(command: List<String>, timeout: Duration): ProcessResult {
        log.debug { "Running: ${command.joinToString(" ")}" }

        val stdoutFile = File.createTempFile("proc-out", ".log")
        val stderrFile = File.createTempFile("proc-err", ".log")
        try {
            val process = ProcessBuilder(command)
                .redirectOutput(stdoutFile)
                .redirectError(stderrFile)
                .start()

            val finished = process.waitFor(timeout.toSeconds(), TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                throw ProcessTimeoutException("Process timed out after ${timeout.toSeconds()}s: ${command.firstOrNull()}")
            }
            return ProcessResult(process.exitValue(), stdoutFile.readText(), stderrFile.readText())
        } finally {
            stdoutFile.delete()
            stderrFile.delete()
        }
    }
}

data class ProcessResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String,
) {
    val isSuccess: Boolean get() = exitCode == 0
}

class ProcessTimeoutException(message: String) : RuntimeException(message)
