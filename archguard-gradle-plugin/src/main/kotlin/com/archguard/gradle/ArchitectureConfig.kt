package com.archguard.gradle

import com.archguard.core.architecture.ArchitectureConfig as CoreArchitectureConfig
import com.archguard.core.architecture.LayerConfig as CoreLayerConfig
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class ArchitectureConfig @Inject constructor(
    private val objects: ObjectFactory,
) {
    var featureRoot: String = "feature"

    private val layers = linkedMapOf<String, LayerConfig>()
    private val forbiddenFolderNames = linkedSetOf<String>()

    fun layer(name: String, action: Action<in LayerConfig>) {
        val layer = layers.getOrPut(name) {
            objects.newInstance(LayerConfig::class.java, name)
        }
        action.execute(layer)
    }

    fun forbid(vararg names: String) {
        names.asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { forbiddenFolderNames.add(it) }
    }

    fun toCoreConfig(): CoreArchitectureConfig {
        val requiredLayers = if (layers.isEmpty()) {
            listOf(
                CoreLayerConfig(name = "presentation", required = true),
                CoreLayerConfig(name = "domain", required = true),
                CoreLayerConfig(name = "data", required = true),
            )
        } else {
            layers.values.map { it.toCoreConfig() }
        }

        return CoreArchitectureConfig(
            featureRoot = featureRoot,
            requiredLayers = requiredLayers,
            forbiddenFolderNames = forbiddenFolderNames.ifEmpty {
                linkedSetOf("helper", "util", "manager")
            },
        )
    }
}
