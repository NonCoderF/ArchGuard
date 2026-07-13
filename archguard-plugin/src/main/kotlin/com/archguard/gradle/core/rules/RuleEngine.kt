package com.archguard.gradle.core.rules

import com.archguard.gradle.core.model.ProjectModel
import com.archguard.gradle.core.model.RuleResult
import com.archguard.gradle.core.model.Violation

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
    fun evaluate(project: ProjectModel): RuleEngineResult {
        val results = rules.map { rule -> rule.evaluate(project) }
        return RuleEngineResult(ruleResults = results)
    }
}

