plugins {
    id("contentria-spring-boot-app-conventions")
}

dependencies {
    implementation(project(":blog-common"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    // Jackson 3 (tools.jackson) + its Boot 4 auto-config (provides the JsonMapper/ObjectMapper bean)
    implementation("org.springframework.boot:spring-boot-starter-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation(libs.kotlin.logging)

    // Cloudflare R2 (S3-compatible): download the source + upload HLS outputs
    implementation(platform(libs.aws.bom))
    implementation(libs.aws.s3)

    // Database
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
