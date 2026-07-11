#!/bin/bash

set -e

BASE="src/main/kotlin/com/archguard"

echo "🚀 Bootstrapping ArchGuard..."

mkdir -p "$BASE"/{plugin,task,engine,model,parser,rules,report}

########################################
# Plugin
########################################

cat > "$BASE/plugin/ArchGuardPlugin.kt" <<'EOF'
package com.archguard.plugin

import com.archguard.task.ArchitectureCheckTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ArchGuardPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.tasks.register(
            "architectureCheck",
            ArchitectureCheckTask::class.java
        ) {
            group = "verification"
            description = "Analyze Android architecture"
        }

    }

}
EOF

########################################
# Task
########################################

cat > "$BASE/task/ArchitectureCheckTask.kt" <<'EOF'
package com.archguard.task

import com.archguard.engine.ArchGuardEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class ArchitectureCheckTask : DefaultTask() {

    @TaskAction
    fun analyze() {

        logger.lifecycle("")
        logger.lifecycle("===================================")
        logger.lifecycle("      ArchGuard v0.1")
        logger.lifecycle("===================================")

        val engine = ArchGuardEngine()

        engine.run(project.projectDir)

        logger.lifecycle("")
    }

}
EOF

########################################
# Engine
########################################

cat > "$BASE/engine/ArchGuardEngine.kt" <<'EOF'
package com.archguard.engine

import java.io.File

class ArchGuardEngine {

    fun run(projectDir: File) {

        val scanner = FileScanner()

        val result = scanner.scan(projectDir)

        println("")
        println("Project : ${projectDir.name}")
        println("Kotlin Files : ${result.kotlinFiles.size}")
        println("Java Files : ${result.javaFiles.size}")
        println("")
        println("Analysis Complete ✅")

    }

}
EOF

cat > "$BASE/engine/FileScanner.kt" <<'EOF'
package com.archguard.engine

import java.io.File

class FileScanner {

    fun scan(root: File): ScanResult {

        val kotlinFiles = mutableListOf<File>()
        val javaFiles = mutableListOf<File>()

        root.walkTopDown().forEach {

            when (it.extension) {

                "kt" -> kotlinFiles.add(it)

                "java" -> javaFiles.add(it)

            }

        }

        return ScanResult(
            kotlinFiles,
            javaFiles
        )

    }

}
EOF

cat > "$BASE/engine/ScanResult.kt" <<'EOF'
package com.archguard.engine

import java.io.File

data class ScanResult(

    val kotlinFiles: List<File>,

    val javaFiles: List<File>

)
EOF

########################################
# Model
########################################

cat > "$BASE/model/ProjectModel.kt" <<'EOF'
package com.archguard.model

data class ProjectModel(

    val classes: List<ClassModel>

)
EOF

cat > "$BASE/model/ClassModel.kt" <<'EOF'
package com.archguard.model

data class ClassModel(

    val name: String,

    val packageName: String,

    val imports: List<String>

)
EOF

########################################
# Parser
########################################

cat > "$BASE/parser/KotlinParser.kt" <<'EOF'
package com.archguard.parser

import java.io.File

class KotlinParser {

    fun parse(file: File) {

        println("Parsing ${file.name}")

    }

}
EOF

########################################
# Rules
########################################

cat > "$BASE/rules/Rule.kt" <<'EOF'
package com.archguard.rules

import com.archguard.model.ProjectModel

interface Rule {

    val id: String

    val description: String

    fun check(project: ProjectModel)

}
EOF

########################################
# Report
########################################

cat > "$BASE/report/ReportGenerator.kt" <<'EOF'
package com.archguard.report

class ReportGenerator {

    fun generate() {

        println("Generating report...")

    }

}
EOF

echo ""
echo "======================================="
echo "✅ ArchGuard bootstrapped successfully!"
echo "======================================="