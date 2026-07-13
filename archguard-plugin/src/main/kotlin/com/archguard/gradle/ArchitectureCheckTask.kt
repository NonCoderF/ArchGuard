package com.archguard.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import com.archguard.gradle.core.architecture.ArchGuardEngine
import com.archguard.gradle.core.architecture.ConsoleReportGenerator
import com.archguard.gradle.core.architecture.HtmlReportGenerator
import kotlin.io.path.toPath
import java.nio.file.Files

/**
 * Lifecycle task exposed by the ArchGuard Gradle plugin.
 *
 * Architecture analysis is implemented in the plugin module under the core
 * package. Keeping the task action here gives consumers a stable Gradle entry
 * point today.
 */
@DisableCachingByDefault(because = "The task reads the current project state and writes reports into the project directory.")
abstract class ArchitectureCheckTask : DefaultTask() {
    @TaskAction
    fun checkArchitecture() {
        val extension = project.extensions.getByType(ArchGuardExtension::class.java)
        val engine = ArchGuardEngine(config = extension.architectureConfig())
        val result = engine.analyze(project.projectDir.toPath())
        val report = ConsoleReportGenerator().render(result)

        report.splitToSequence('\n').forEach { line ->
            logger.lifecycle(line)
        }

        if (extension.htmlReportEnabled()) {
            val htmlReport = HtmlReportGenerator().render(result)
            val outputFile = project.projectDir.toPath().resolve(extension.htmlReportPath())
            Files.createDirectories(outputFile.parent ?: project.projectDir.toPath())
            Files.writeString(outputFile, htmlReport)
            logger.lifecycle("HTML report written to ${outputFile.toAbsolutePath()}")
        }
    }
}

