package com.archguard.core.architecture

import java.nio.file.Path

class ArchGuardEngine(
    private val folderScanner: FolderScanner = FolderScanner(),
    private val config: ArchitectureConfig = ArchitectureConfig(),
) {
    fun analyze(projectRoot: Path): ArchitectureAnalysisResult {
        val sourceRoot = folderScanner.scan(projectRoot)
        val featureStructureRule = FeatureStructureRule(config)
        val featureResults = featureStructureRule.inspect(sourceRoot)
        val ruleEngine = RuleEngine(listOf(featureStructureRule))
        val ruleEngineResult = ruleEngine.evaluate(sourceRoot)

        return ArchitectureAnalysisResult(
            projectRoot = projectRoot.toAbsolutePath().normalize(),
            sourceRoot = sourceRoot,
            featureResults = featureResults,
            ruleEngineResult = ruleEngineResult,
        )
    }
}
