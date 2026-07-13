package com.archguard.gradle.core.model

data class RuleResult(
    val ruleId: String,
    val violations: List<Violation> = emptyList(),
)

