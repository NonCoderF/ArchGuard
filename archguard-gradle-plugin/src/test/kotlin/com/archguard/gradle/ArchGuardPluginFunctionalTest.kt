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
    fun `architectureCheck works from kotlin dsl`() {
        val projectDirectory = createTempDirectory("archguard-test").toFile()
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

                    layer("presentation") {
                        required = true
                    }

                    layer("domain") {
                        required = true
                    }

                    layer("data") {
                        required = true
                    }

                    forbid(
                        "helper",
                        "util",
                        "manager"
                    )
                }
            }
            """.trimIndent(),
        )

        configureSampleProject(projectDirectory)

        val result = runArchitectureCheck(projectDirectory)
        val reportFile = projectDirectory.toPath().resolve("archguard-report.html").toFile()

        assertSuccess(result, reportFile)
    }

    @Test
    fun `architectureCheck works from groovy dsl`() {
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

                    layer("presentation") {
                        required = true
                    }

                    layer("domain") {
                        required = true
                    }

                    layer("data") {
                        required = true
                    }

                    forbid(
                        "helper",
                        "util",
                        "manager"
                    )
                }
            }
            """.trimIndent(),
        )

        configureSampleProject(projectDirectory)

        val result = runArchitectureCheck(projectDirectory)
        val reportFile = projectDirectory.toPath().resolve("archguard-report.html").toFile()

        assertSuccess(result, reportFile)
    }

    private fun configureSampleProject(projectDirectory: java.io.File) {
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
        assertTrue(result.output.contains("Features : 2"))
        assertTrue(result.output.contains("profile"))
        assertTrue(result.output.contains("Missing"))
        assertTrue(result.output.contains("domain"))
        assertTrue(result.output.contains("Violations : 1"))
        assertTrue(reportFile.exists())
        assertTrue(reportFile.readText().contains("<!doctype html>"))
        assertTrue(reportFile.readText().contains("profile"))
        assertTrue(reportFile.readText().contains("Missing folders"))
    }
}
