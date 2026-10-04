pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
}

rootProject.name = "backend"
include("blog-common")
include("blog-api")
include("blog-batch")
include("blog-worker")

include("core")
include("app:api", "app:batch")
