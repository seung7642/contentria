plugins {
    id("contentria-spring-boot-app-conventions")
}

dependencies {
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-batch")
}
