# Prebuilt jar required: run `./gradlew :blog-batch:bootJar` before building this image.
FROM eclipse-temurin:21-jre-alpine

RUN addgroup --system contentria && adduser --system --ingroup contentria contentria
WORKDIR /app

ARG JAR_FILE=blog-batch/build/libs/blog-batch-*.jar
COPY ${JAR_FILE} app.jar

# No EXPOSE: batch jobs don't serve HTTP unless Actuator/Batch Admin is enabled.
USER contentria
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
