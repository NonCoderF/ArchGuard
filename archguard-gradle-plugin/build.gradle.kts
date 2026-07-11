plugins {
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "2.1.1"
    kotlin("jvm") version "2.2.0"
}

group = "io.github.noncoderf.archguard"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(project(":archguard-core"))
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
}

gradlePlugin {
    website.set("https://github.com/NonCoderF/ArchGuard")
    vcsUrl.set("https://github.com/NonCoderF/ArchGuard")

    plugins {
        create("archGuard") {
            id = "io.github.noncoderf.archguard.gradle"
            implementationClass = "com.archguard.gradle.ArchGuardPlugin"
            displayName = "ArchGuard"
            description = "Gradle integration for ArchGuard architecture analysis."
            tags.set(listOf("architecture", "architecture-linting", "analysis"))
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
