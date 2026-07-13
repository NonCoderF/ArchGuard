import org.gradle.api.tasks.Exec

plugins {
    kotlin("jvm") version "2.2.0" apply false
}

group = "io.github.noncoderf.archguard"
version = "0.1.3"

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
}

tasks.register<Exec>("archGuardCheck") {
    group = "verification"
    description = "Runs the sample project ArchGuard check."
    dependsOn(":archguard-plugin:build")

    val isWindows = System.getProperty("os.name").lowercase().contains("windows")
    commandLine(
        file(if (isWindows) "gradlew.bat" else "gradlew").absolutePath,
        "-p",
        "sample-project",
        "archGuardCheck",
    )
}
