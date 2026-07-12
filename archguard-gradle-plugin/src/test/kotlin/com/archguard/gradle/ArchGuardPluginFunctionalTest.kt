package com.archguard.gradle

import kotlin.io.path.createDirectories
import kotlin.io.path.createTempDirectory
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

class ArchGuardPluginFunctionalTest {
    @Test
    fun `feature dsl works from kotlin dsl`() {
        val projectDirectory = createTempDirectory("archguard-kotlin-test").toFile()
        projectDirectory.toPath().resolve("settings.gradle.kts").writeText(
            "rootProject.name = \"functional-test\"\n",
        )
        projectDirectory.toPath().resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("io.github.noncoderf.archguard.gradle")
            }

            archGuard {
                reports {
                    html {
                        enabled = true
                        outputPath = "archguard-report.html"
                    }
                }

                architecture {
                    featureRoot = "feature"

                    feature("login") {
                        requiredLayer("presentation")
                        requiredLayer("domain")
                        requiredLayer("data")

                        feature("chat") {
                            requiredLayer("ui")
                            requiredLayer("socket")
                        }
                    }
                }
            }
            """.trimIndent(),
        )

        configureSampleProject(projectDirectory)

        val result = runArchitectureCheck(projectDirectory)
        val reportFile = projectDirectory.toPath().resolve("archguard-report.html").toFile()

        assertSuccess(result, reportFile)
        assertTrue(result.output.contains("login"))
        assertTrue(result.output.contains("chat"))
        assertTrue(result.output.contains("socket"))
    }

    @Test
    fun `feature dsl works from groovy dsl`() {
        val projectDirectory = createTempDirectory("archguard-groovy-test").toFile()
        projectDirectory.toPath().resolve("settings.gradle").writeText(
            "rootProject.name = 'functional-test'\n",
        )
        projectDirectory.toPath().resolve("build.gradle").writeText(
            """
            plugins {
                id 'io.github.noncoderf.archguard.gradle'
            }

            archGuard {
                reports {
                    html {
                        enabled = true
                        outputPath = "archguard-report.html"
                    }
                }

                architecture {
                    featureRoot = "feature"

                    feature("login") {
                        requiredLayer("presentation")
                        requiredLayer("domain")
                        requiredLayer("data")

                        feature("chat") {
                            requiredLayer("ui")
                            requiredLayer("socket")
                        }
                    }
                }
            }
            """.trimIndent(),
        )

        configureSampleProject(projectDirectory)

        val result = runArchitectureCheck(projectDirectory)
        val reportFile = projectDirectory.toPath().resolve("archguard-report.html").toFile()

        assertSuccess(result, reportFile)
        assertTrue(result.output.contains("login"))
        assertTrue(result.output.contains("chat"))
    }

    @Test
    fun `legacy layer dsl still maps to anonymous feature`() {
        val projectDirectory = createTempDirectory("archguard-legacy-test").toFile()
        projectDirectory.toPath().resolve("settings.gradle").writeText(
            "rootProject.name = 'functional-test'\n",
        )
        projectDirectory.toPath().resolve("build.gradle").writeText(
            """
            plugins {
                id 'io.github.noncoderf.archguard.gradle'
            }

            archGuard {
                architecture {
                    featureRoot = "feature"

                    layer("presentation") {
                        required = true
                    }

                    layer("domain") {
                        required = true
                    }

                    layer("data") {
                        required = true
                    }
                }
            }
            """.trimIndent(),
        )

        configureLegacyProject(projectDirectory)

        val result = runArchitectureCheck(projectDirectory)

        assertEquals(TaskOutcome.SUCCESS, result.task(":architectureCheck")?.outcome)
        assertTrue(result.output.contains("Features : 2"))
        assertTrue(result.output.contains("profile"))
        assertTrue(result.output.contains("Missing"))
    }

    private fun configureSampleProject(projectDirectory: java.io.File) {
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/presentation").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/domain").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/data").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/chat/ui").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/chat/socket").createDirectories()
    }

    private fun configureLegacyProject(projectDirectory: java.io.File) {
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/presentation").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/domain").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/login/data").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/profile/presentation").createDirectories()
        projectDirectory.toPath().resolve("src/main/kotlin/feature/profile/data").createDirectories()
    }

    private fun runArchitectureCheck(projectDirectory: java.io.File): BuildResult {
        return GradleRunner.create()
            .withProjectDir(projectDirectory)
            .withPluginClasspath()
            .withArguments("--stacktrace", "architectureCheck")
            .build()
    }

    private fun assertSuccess(result: BuildResult, reportFile: java.io.File) {
        assertEquals(TaskOutcome.SUCCESS, result.task(":architectureCheck")?.outcome)
        assertTrue(result.output.contains("ArchGuard v0.0.1"))
        assertTrue(result.output.contains("Features :"))
        assertTrue(result.output.contains("Violations : 0"))
        assertTrue(reportFile.exists())
        assertTrue(reportFile.readText().contains("<!doctype html>"))
        assertTrue(reportFile.readText().contains("Feature Tree"))
    }
}
