package com.archguard.core.architecture

import java.nio.file.Path

data class ArchitectureAnalysisResult(
    val projectRoot: Path,
    val sourceRoot: FolderNode,
    val featureResults: List<FeatureValidation>,
    val ruleEngineResult: RuleEngineResult,
) {
    val allFeatures: List<FeatureValidation>
        get() = featureResults.flatMap { it.flatten().toList() }

    val passedFeatures: Int
        get() = allFeatures.count { it.isPassed }

    val failedFeatures: Int
        get() = allFeatures.count { !it.isPassed }

    val violations: List<Violation>
        get() = ruleEngineResult.violations
}
