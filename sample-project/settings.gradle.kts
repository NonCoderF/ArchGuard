pluginManagement {
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.2.0"
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("../archguard-plugin")
}

rootProject.name = "sample-project"
