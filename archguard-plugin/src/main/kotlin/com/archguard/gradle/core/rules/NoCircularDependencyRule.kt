package com.archguard.gradle.core.rules

import com.archguard.gradle.core.model.ProjectModel
import com.archguard.gradle.core.model.RuleResult
import com.archguard.gradle.core.model.Violation

class NoCircularDependencyRule : Rule {
    override val id: String = "NoCircularDependency"
    override val title: String = "Circular dependency detection"
    override val description: String = "Reports a violation when the dependency graph contains a cycle."

    override fun evaluate(project: ProjectModel): RuleResult {
        val cycle = project.dependencyGraph.findCycle()
        return if (cycle == null) {
            RuleResult(ruleId = id)
        } else {
            RuleResult(
                ruleId = id,
                violations = listOf(
                    Violation(
                        ruleId = id,
                        message = "Circular dependency detected: ${cycle.joinToString(" -> ")}",
                    ),
                ),
            )
        }
    }
}

