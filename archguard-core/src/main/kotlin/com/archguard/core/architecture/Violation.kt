package com.archguard.core.architecture

data class Violation(
    val ruleId: String,
    val message: String,
    val location: String,
)
