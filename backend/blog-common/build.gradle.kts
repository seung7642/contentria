plugins {
    id("contentria-spring-library-conventions")
}

dependencies {
    implementation(project(":core"))

    // Spring Boot 기본
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin 관련
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation(libs.kotlin.logging)

    // UUID
    implementation(libs.uuid.creator)

    // Slug 생성
    implementation(libs.slugify)

    // Cloudflare R2 (S3-compatible): shared S3Client bean for blog-api/blog-batch/blog-worker
    implementation(platform(libs.aws.bom))
    implementation(libs.aws.s3)

    // 테스트 종속성 - 명시적으로 JUnit 프레임워크 지정
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    if (project.hasProperty("runRealMailTests") && project.property("runRealMailTests") == "true") {
        useJUnitPlatform {
            includeTags("real-mail-test")
        }
    } else {
        useJUnitPlatform {
            excludeTags("real-mail-test")
        }
    }
}
