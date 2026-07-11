package com.archguard.core.architecture

data class RuleResult(
    val ruleId: String,
    val violations: List<Violation> = emptyList(),
)
