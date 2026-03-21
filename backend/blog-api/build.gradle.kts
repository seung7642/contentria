plugins {
    id("kotlin-common-conventions")
}

dependencies {
    implementation(project(":blog-common"))

    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
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

    // 데이터베이스
    runtimeOnly("com.h2database:h2") // 개발용, 필요시 변경
    runtimeOnly("org.postgresql:postgresql") // 프로덕션용

    // Rate Limiting
    implementation("com.giffing.bucket4j.spring.boot.starter:bucket4j-spring-boot-starter:0.13.0")
    implementation("com.github.ben-manes.caffeine:jcache:3.2.2")

    // Markdown parser
    implementation("org.commonmark:commonmark:0.27.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.27.0")
    implementation("org.commonmark:commonmark-ext-gfm-strikethrough:0.27.0")
    implementation("org.commonmark:commonmark-ext-autolink:0.27.0")
    implementation("org.commonmark:commonmark-ext-footnotes:0.27.0")
    implementation("org.commonmark:commonmark-ext-heading-anchor:0.27.0")
    implementation("org.commonmark:commonmark-ext-ins:0.27.0")
    implementation("org.commonmark:commonmark-ext-yaml-front-matter:0.27.0")
    implementation("org.commonmark:commonmark-ext-image-attributes:0.27.0")
    implementation("org.commonmark:commonmark-ext-task-list-items:0.27.0")

    // 테스트 종속성 - 명시적으로 JUnit 프레임워크 지정
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}