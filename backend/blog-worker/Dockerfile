# Prebuilt jar required: run `./gradlew :blog-worker:bootJar` before building this image.
FROM eclipse-temurin:21-jre-alpine

# ffmpeg/ffprobe are invoked as subprocesses (see FfmpegTranscoder).
RUN apk add --no-cache ffmpeg
RUN addgroup --system contentria && adduser --system --ingroup contentria contentria
WORKDIR /app

# Scratch space for one transcode job (raw <= 500 MB + outputs); mount an emptyDir here in K8s.
RUN mkdir -p /tmp/transcode && chown contentria:contentria /tmp/transcode

ARG JAR_FILE=blog-worker/build/libs/blog-worker-*.jar
COPY ${JAR_FILE} app.jar

# No EXPOSE: the worker pulls from Cloudflare Queue, it doesn't serve HTTP.
USER contentria
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
