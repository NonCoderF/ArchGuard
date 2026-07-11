package com.archguard.core.architecture

data class RuleEngineResult(
    val ruleResults: List<RuleResult>,
) {
    val violations: List<Violation>
        get() = ruleResults.flatMap { it.violations }

    val hasViolations: Boolean
        get() = violations.isNotEmpty()
}

class RuleEngine(
    private val rules: List<Rule>,
) {
    fun evaluate(project: FolderNode): RuleEngineResult {
        val results = rules.map { rule -> rule.evaluate(project) }
        return RuleEngineResult(ruleResults = results)
    }
}
