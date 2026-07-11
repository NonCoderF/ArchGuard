package com.archguard.core.architecture

data class ArchitectureConfig(
    val featureRoot: String = "feature",
    val requiredLayers: List<LayerConfig> = listOf(
        LayerConfig(name = "presentation", required = true),
        LayerConfig(name = "domain", required = true),
        LayerConfig(name = "data", required = true),
    ),
    val forbiddenFolderNames: Set<String> = setOf("helper", "util", "manager"),
)

data class LayerConfig(
    val name: String,
    val required: Boolean = false,
)
