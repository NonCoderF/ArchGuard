package com.archguard.core.architecture

import java.nio.file.Path

data class ArchitectureAnalysisResult(
    val projectRoot: Path,
    val sourceRoot: FolderNode,
    val featureResults: List<FeatureValidation>,
    val ruleEngineResult: RuleEngineResult,
) {
    val passedFeatures: Int
        get() = featureResults.count { it.isPassed }

    val failedFeatures: Int
        get() = featureResults.count { !it.isPassed }

    val violations: List<Violation>
        get() = ruleEngineResult.violations
}
