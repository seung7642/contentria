plugins {
    id("kotlin-common-conventions")
}

dependencies {
    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin 관련
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    // UUID
    implementation("com.github.f4b6a3:uuid-creator:6.1.0")

    // 메일 전송
    implementation("com.mailgun:mailgun-java:1.1.6")

    // Slug 생성
    implementation("com.github.slugify:slugify:3.0.7")

    // 테스트 종속성 - 명시적으로 JUnit 프레임워크 지정
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}