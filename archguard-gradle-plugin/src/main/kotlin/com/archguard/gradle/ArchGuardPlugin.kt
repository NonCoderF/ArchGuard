package com.archguard.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ArchGuardPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("archGuard", ArchGuardExtension::class.java)
        project.tasks.register("architectureCheck", ArchitectureCheckTask::class.java) { task ->
            task.group = "verification"
            task.description = "Checks the project architecture using ArchGuard."
        }
    }
}
