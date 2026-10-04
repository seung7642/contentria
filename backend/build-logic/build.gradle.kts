plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

// Precompiled script plugins (src/main/kotlin/*.gradle.kts) 내에서는 `plugins { id(...) }` 선언 시 버전을 명시할 수 없다.
// 플러그인 구현체가 build-logic의 클래스패스에 미리 로드되어 있어야 하므로,
// 하위 컨벤션 스크립트에서 사용할 플러그인 의존성을 여기에 명시한다.
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${libs.versions.spring.boot.get()}")
    implementation("io.spring.gradle:dependency-management-plugin:${libs.versions.spring.dependency.management.get()}")

    // Kotlin Plugins
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.asProvider().get()}")
    implementation("org.jetbrains.kotlin:kotlin-allopen:${libs.versions.kotlin.asProvider().get()}")
    implementation("org.jetbrains.kotlin:kotlin-noarg:${libs.versions.kotlin.asProvider().get()}")
}