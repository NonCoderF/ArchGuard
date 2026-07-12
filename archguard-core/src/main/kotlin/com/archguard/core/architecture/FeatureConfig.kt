package com.archguard.core.architecture

data class ArchitectureConfig(
    val featureRoot: String = "feature",
    val features: List<FeatureConfig> = listOf(defaultAnonymousFeature()),
)

data class FeatureConfig(
    val name: String = "",
    val requiredLayers: List<String> = emptyList(),
    val childFeatures: List<FeatureConfig> = emptyList(),
    val forbiddenFolders: Set<String> = emptySet(),
    val customRules: List<FeatureRuleConfig> = emptyList(),
) {
    val isAnonymousTemplate: Boolean
        get() = name.isBlank()
}

data class FeatureRuleConfig(
    val name: String,
    val parameters: Map<String, String> = emptyMap(),
)

fun defaultAnonymousFeature(): FeatureConfig = FeatureConfig(
    name = "",
    requiredLayers = listOf("presentation", "domain", "data"),
    forbiddenFolders = setOf("helper", "util", "manager"),
)
