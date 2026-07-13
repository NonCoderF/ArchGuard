package com.archguard.gradle.core.architecture

class ConsoleReportGenerator {
    fun render(result: ArchitectureAnalysisResult): String {
        return buildString {
            appendLine("ArchGuard v0.0.1")
            appendLine()
            appendLine("Scanning folders...")
            appendLine()
            appendLine("Found")
            appendLine()
            appendLine("Features : ${result.allFeatures.size}")
            appendLine()
            appendLine("Running rules...")
            appendLine()

            result.featureResults.forEach { feature ->
                renderFeature(feature, 0)
            }

            appendLine()
            appendLine("Summary")
            appendLine()
            appendLine("Passed : ${result.passedFeatures}")
            appendLine("Failed : ${result.failedFeatures}")
            appendLine("Violations : ${result.violations.size}")
        }
    }

    private fun StringBuilder.renderFeature(feature: FeatureValidation, depth: Int) {
        val indent = "  ".repeat(depth)
        appendLine("${indent}${if (feature.isPassed) "âœ“" else "âœ—"} ${feature.name}")

        feature.requiredLayers.forEach { layer ->
            val status = if (layer.present) "âœ“" else "âœ— Missing"
            appendLine("${indent}  - ${layer.name} $status")
        }

        if (feature.violations.isNotEmpty()) {
            feature.violations.forEach { violation ->
                appendLine("${indent}  ! ${violation.message}")
            }
        }

        feature.childFeatures.forEach { child ->
            renderFeature(child, depth + 1)
        }
    }
}

