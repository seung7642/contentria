plugins {
    id("kotlin-common-conventions")
}

dependencies {
    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.3")
    implementation("com.auth0:java-jwt:4.4.0")

    // OAuth2 클라이언트
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // HTTP 클라이언트
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Kotlin 관련
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    implementation("com.mailgun:mailgun-java:1.1.6")

    // 데이터베이스
    runtimeOnly("com.h2database:h2") // 개발용, 필요시 변경
    runtimeOnly("org.postgresql:postgresql") // 프로덕션용

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Rate Limiting
    implementation("com.bucket4j:bucket4j_jdk17-core:8.14.0")
    implementation("com.bucket4j:bucket4j_jdk17-redis-common:8.14.0")
    implementation("com.bucket4j:bucket4j_jdk17-lettuce:8.14.0")

    // 테스트 종속성 - 명시적으로 JUnit 프레임워크 지정
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}