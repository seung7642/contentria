import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("contentria-kotlin-common-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.jetbrains.kotlin.plugin.spring") // kotlin("plugin.spring") 대신 정식 ID 사용
}

// 라이브러리 모듈은 bootJar를 끄고 일반 jar를 생성해야 다른 모듈에서 참조 가능
tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}