package com.archguard.core.architecture

class ConsoleReportGenerator {
    fun render(result: ArchitectureAnalysisResult): String {
        return buildString {
            appendLine("ArchGuard v0.0.1")
            appendLine()
            appendLine("Scanning folders...")
            appendLine()
            appendLine("Found")
            appendLine()
            appendLine("Features : ${result.featureResults.size}")
            appendLine()
            appendLine("Running rules...")
            appendLine()

            result.featureResults.forEach { feature ->
                if (feature.isPassed) {
                    appendLine("✓ ${feature.name}")
                } else {
                    appendLine("✗ ${feature.name}")
                    appendLine()
                    appendLine("Missing")
                    appendLine()
                    feature.missingFolders.forEach { missingFolder ->
                        appendLine(missingFolder)
                    }
                    appendLine()
                }
            }

            appendLine("Summary")
            appendLine()
            appendLine("Passed : ${result.passedFeatures}")
            appendLine("Failed : ${result.failedFeatures}")
            appendLine("Violations : ${result.violations.size}")
        }
    }
}
