package com.archguard.gradle.core.architecture

import java.nio.file.Path

data class LayerValidation(
    val name: String,
    val present: Boolean,
)

data class FeatureValidation(
    val name: String,
    val path: Path,
    val requiredLayers: List<LayerValidation> = emptyList(),
    val childFeatures: List<FeatureValidation> = emptyList(),
    val violations: List<Violation> = emptyList(),
) {
    val missingFolders: List<String>
        get() = requiredLayers.filterNot { it.present }.map { it.name }

    val isPassed: Boolean
        get() = missingFolders.isEmpty() && childFeatures.all { it.isPassed } && violations.isEmpty()

    fun flatten(): Sequence<FeatureValidation> = sequence {
        yield(this@FeatureValidation)
        childFeatures.forEach { child -> yieldAll(child.flatten()) }
    }

    fun allViolations(ruleId: String): List<Violation> = buildList {
        missingFolders.forEach { missingFolder ->
            add(
                Violation(
                    ruleId = ruleId,
                    message = "Missing folder: $missingFolder",
                    location = path.toString(),
                ),
            )
        }
        addAll(violations)
        childFeatures.forEach { child -> addAll(child.allViolations(ruleId)) }
    }
}

