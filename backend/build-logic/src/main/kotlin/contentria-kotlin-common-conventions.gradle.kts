plugins {
    kotlin("jvm")
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

group = "com.contentria"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        val javaVersion = libs.findVersion("java").get().requiredVersion
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

tasks {
    // .env files live in src/main/resources so the IDE can load them, but they must never be
    // packaged: processResources would copy them into BOOT-INF/classes (→ Docker image layers),
    // leaking real secrets. Samples are excluded too — nothing in the jar needs them.
    withType<ProcessResources> {
        exclude("**/.env", "**/.env.sample")
    }

    named<Test>("test") {
        useJUnitPlatform()

        maxHeapSize = "1G"

        // 테스트 코드가 있는 디렉토리에 실제 실행할 테스트(`@Test`)가 없어도 빌드 실패 방지
        failOnNoDiscoveredTests = false

        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}