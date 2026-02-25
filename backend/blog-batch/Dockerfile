# =============================================================================
# [ DOCKERFILE ARCHITECTURE & INTENT ]
#
# This Dockerfile utilizes a "Multi-Stage Build" pattern and Spring Boot's
# "Layered JAR" feature to create a highly optimized, secure, and production-
# ready Docker image.
#
# Why is it designed this way?
# 1. Image Size (Multi-Stage): We use a heavy JDK image for building the app
#    (Stage 1), but discard it. The final image (Stage 2) uses a lightweight
#    JRE image, significantly reducing the final image size and attack surface.
# 2. Build Speed (Caching): By carefully copying build files (gradlew, etc.)
#    before the source code, we leverage Docker's layer caching. If source code
#    changes, dependencies are NOT re-downloaded.
# 3. Layered JARs: Spring Boot 2.3+ allows splitting the JAR into layers
#    (dependencies, spring-boot-loader, application logic). This means pushing
#    updates to your registry is blazingly fast because only the 'application'
#    layer changes.
# 4. Security: The application runs as a non-root user ('spring') following
#    the principle of least privilege.
# =============================================================================

# =============================================================================
# 1. BUILD STAGE: Application Compilation and Layered JAR Extraction
# =============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# =============================================================================
# 1.1. Setup Working Directory and Copy Gradle Wrapper/Configuration Files
# =============================================================================
WORKDIR /app
COPY gradlew settings.gradle.kts /app/
COPY gradle /app/gradle/

# =============================================================================
# 1.2. Copy Module Build Scripts
#      Required for Gradle to resolve inter-module dependencies accurately.
# =============================================================================
COPY buildSrc /app/buildSrc/
COPY blog-common/build.gradle.kts blog-common/
COPY blog-api/build.gradle.kts blog-api/
COPY blog-batch/build.gradle.kts blog-batch/

# =============================================================================
# 1.3. Download Gradle Dependencies
#      Specifically downloads dependencies for the `blog-batch` module.
# =============================================================================
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon --stacktrace :blog-batch:dependencies

# =============================================================================
# 1.4. Copy Source Code for Relevant Modules
#      Copies source code for `blog-batch` and its dependency `blog-common`.
# =============================================================================
COPY blog-common/src blog-common/src
COPY blog-batch/src blog-batch/src

# =============================================================================
# 1.5. Build Spring Boot JAR for blog-batch Module
#      Executes the `bootJar` task specifically for the batch module.
# =============================================================================
RUN ./gradlew --no-daemon -x test :blog-batch:bootJar

# =============================================================================
# 1.6. Extract Spring Boot Layered JAR
#      Separates the built JAR into cacheable layers.
# =============================================================================
ARG JAR_FILE=/app/blog-batch/build/libs/blog-batch-*.jar
RUN java -Djarmode=layertools -jar ${JAR_FILE} extract --destination blog-batch/build/extracted

# =============================================================================
# 2. RUN STAGE: Production-Ready Batch Execution Environment
# =============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

# =============================================================================
# 2.1. Setup Working Directory and Create Non-Root User
# =============================================================================
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring

# =============================================================================
# 2.2. COPY Layered JAR Contents from Build Stage
#      Order matters: Least frequently changed layers first.
# =============================================================================
COPY --from=builder /app/blog-batch/build/extracted/dependencies/ ./
COPY --from=builder /app/blog-batch/build/extracted/spring-boot-loader/ ./
COPY --from=builder /app/blog-batch/build/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/blog-batch/build/extracted/application/ ./

# =============================================================================
# 2.3. Configure Runtime Environment
#      Switch to non-root user.
#      Note: EXPOSE is commented out because Batch applications typically do
#      not serve HTTP traffic unless Spring Actuator or Batch Admin is enabled.
# =============================================================================
USER spring

# =============================================================================
# 2.4. Define Application Entrypoint and Default Command
#      Specifies how the Batch application starts. Spring Batch jobs can be
#      triggered via command line arguments passed into CMD [].
# =============================================================================
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
CMD []