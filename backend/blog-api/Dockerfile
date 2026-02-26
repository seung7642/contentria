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
#    This stage uses a full JDK to compile the Spring Boot application
#    and then extracts its components into layers for optimized Docker caching.
# =============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# =============================================================================
# 1.1. Setup Working Directory and Copy Gradle Wrapper/Configuration Files
#      These files are copied early to leverage Docker's build cache for
#      Gradle dependency resolution. Changes here will invalidate cache
#      for dependency downloading.
# =============================================================================
WORKDIR /app
COPY gradlew settings.gradle.kts /app/
COPY gradle /app/gradle/

# =============================================================================
# 1.2. Copy Module Build Scripts
#      All `build.gradle.kts` files are copied to allow Gradle to understand
#      the multi-module project structure and resolve inter-module dependencies.
# =============================================================================
COPY buildSrc /app/buildSrc/
COPY blog-common/build.gradle.kts blog-common/
COPY blog-api/build.gradle.kts blog-api/
COPY blog-batch/build.gradle.kts blog-batch/

# =============================================================================
# 1.3. Download Gradle Dependencies
#      Execute `dependencies` task to download and cache all required libraries.
#      `--no-daemon` is used for faster builds in container environments.
#      `-x test` skips unit tests during this build stage.
# =============================================================================
RUN chmod +x ./gradlew \
    && ./gradlew --no-daemon --stacktrace :blog-api:dependencies

# =============================================================================
# 1.4. Copy Source Code for Relevant Modules
#      Source code for the `blog-api` and its direct dependencies (like `blog-common`)
#      is copied. This layer's cache is invalidated upon any source code change.
# =============================================================================
COPY blog-common/src blog-common/src
COPY blog-api/src blog-api/src

# =============================================================================
# 1.5. Build Spring Boot JAR for blog-api Module
#      The `bootJar` task is executed for the specific `blog-api` module.
# =============================================================================
RUN ./gradlew --no-daemon -x test :blog-api:bootJar

# =============================================================================
# 1.6. Extract Spring Boot Layered JAR
#      This step utilizes Spring Boot's "layertools" to separate the JAR into
#      distinct layers (dependencies, loader, snapshot-dependencies, application)
#      which drastically improves Docker image build times and caching.
# =============================================================================
ARG JAR_FILE=/app/blog-api/build/libs/blog-api-*.jar
RUN java -Djarmode=layertools -jar ${JAR_FILE} extract --destination blog-api/build/extracted

# =============================================================================
# 2. RUN STAGE: Production-Ready Application Execution Environment
#    This stage creates a minimal image containing only the JRE and the
#    extracted application layers, optimizing for size and security.
# =============================================================================
FROM eclipse-temurin:21-jre-alpine AS runner

# =============================================================================
# 2.1. Setup Working Directory and Create Non-Root User
#      A non-root user (`spring`) is created for enhanced security, adhering
#      to the principle of least privilege.
# =============================================================================
WORKDIR /app
RUN addgroup --system spring && adduser --system --ingroup spring spring

# =============================================================================
# 2.2. COPY Layered JAR Contents from Build Stage
#      Layers are copied in an order that maximizes Docker's cache efficiency,
#      placing less frequently changing layers (dependencies) first.
# =============================================================================
COPY --from=builder /app/blog-api/build/extracted/dependencies/ ./
COPY --from=builder /app/blog-api/build/extracted/spring-boot-loader/ ./
COPY --from=builder /app/blog-api/build/extracted/snapshot-dependencies/ ./
COPY --from=builder /app/blog-api/build/extracted/application/ ./

# =============================================================================
# 2.3. Configure Runtime Environment
#      Switch to the non-root user and expose the application's listening port.
# =============================================================================
USER spring
EXPOSE 8080

# =============================================================================
# 2.4. Define Application Entrypoint and Default Command
#      Specifies how the Spring Boot application is started within the container.
# =============================================================================
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
CMD []