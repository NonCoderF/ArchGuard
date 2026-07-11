package com.archguard.core.config

/** Tool configuration independent from Gradle. */
data class ArchGuardConfig(
    val ignoredPackages: List<String> = emptyList(),
    val enabledRules: Set<String> = emptySet(),
)
