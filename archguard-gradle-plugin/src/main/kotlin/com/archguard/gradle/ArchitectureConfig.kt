package com.archguard.gradle

import com.archguard.core.architecture.ArchitectureConfig as CoreArchitectureConfig
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class ArchitectureConfig @Inject constructor(
    private val objects: ObjectFactory,
) {
    var featureRoot: String = "feature"

    private val features: MutableList<FeatureConfig> = mutableListOf()
    private val legacyAnonymousFeature: FeatureConfig = objects.newInstance(FeatureConfig::class.java, objects, "")

    @JvmSynthetic
    fun feature(name: String, action: Action<in FeatureConfig>) {
        val feature = objects.newInstance(FeatureConfig::class.java, objects, name)
        action.execute(feature)
        features += feature
    }

    fun feature(name: String, closure: Closure<*>) {
        val feature = objects.newInstance(FeatureConfig::class.java, objects, name)
        configureClosure(closure, feature)
        features += feature
    }

    @JvmSynthetic
    fun layer(name: String, action: Action<in LayerConfig>) {
        val layer = objects.newInstance(LayerConfig::class.java, name)
        action.execute(layer)
        if (layer.required) {
            legacyAnonymousFeature.requiredLayer(name)
        }
    }

    fun layer(name: String, closure: Closure<*>) {
        val layer = objects.newInstance(LayerConfig::class.java, name)
        configureClosure(closure, layer)
        if (layer.required) {
            legacyAnonymousFeature.requiredLayer(name)
        }
    }

    fun forbid(vararg names: String) {
        legacyAnonymousFeature.forbid(*names)
    }

    fun hasContent(): Boolean {
        return features.isNotEmpty() || legacyAnonymousFeature.hasContent()
    }

    fun toCoreConfig(): CoreArchitectureConfig {
        val mappedFeatures = buildList {
            if (legacyAnonymousFeature.hasContent()) {
                add(legacyAnonymousFeature.toCoreConfig())
            }
            addAll(features.map { it.toCoreConfig() })
        }

        return if (mappedFeatures.isEmpty()) {
            CoreArchitectureConfig(featureRoot = featureRoot)
        } else {
            CoreArchitectureConfig(
                featureRoot = featureRoot,
                features = mappedFeatures,
            )
        }
    }
}
