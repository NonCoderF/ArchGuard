plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "2.2.0"
}

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
    implementation("com.archguard:archguard-core:0.1.0-SNAPSHOT")
    testImplementation(gradleTestKit())
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("archGuard") {
            id = "com.archguard.plugin"
            implementationClass = "com.archguard.gradle.ArchGuardPlugin"
            displayName = "ArchGuard"
            description = "Gradle integration for ArchGuard architecture analysis."
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    dependsOn(":archguard-core:jar")
    useJUnitPlatform()
}
