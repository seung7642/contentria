plugins {
    id("contentria-spring-boot-app-conventions")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":blog-common"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(libs.kotlin.logging)

    // Cloudflare R2 (S3-compatible): delete video objects during GC
    implementation(platform(libs.aws.bom))
    implementation(libs.aws.s3)

    // Database
    runtimeOnly("org.postgresql:postgresql")

    // Testcontainers (version managed by the Spring Boot BOM, same as blog-api)
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.batch:spring-batch-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Jar>("jar") { enabled = false }