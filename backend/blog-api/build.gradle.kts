plugins {
    id("contentria-spring-boot-app-conventions")
//    kotlin("kapt")
}

dependencies {
    implementation(project(":blog-common"))

    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-jackson")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation(libs.jjwt.api)
    implementation(libs.jjwt.impl)
    implementation(libs.jjwt.jackson)
    implementation(libs.java.jwt)

    // OAuth2 클라이언트
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-client")

    // Kotlin 관련
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation(libs.kotlin.logging)

    // 데이터베이스
    runtimeOnly("com.h2database:h2") // 개발용, 필요시 변경
    runtimeOnly("org.postgresql:postgresql") // 프로덕션용

    // Rate Limiting
    implementation(libs.bucket4j.starter)
    implementation(libs.caffeine.jcache)

    // Markdown parser
    implementation(libs.bundles.commonmark)

    // AWS S3 SDK (Cloudflare R2 compatible)
    implementation(platform(libs.aws.bom))
    implementation(libs.aws.s3)

    // 테스트 종속성 - 명시적으로 JUnit 프레임워크 지정
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")
}