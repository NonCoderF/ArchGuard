package com.archguard.gradle

import com.archguard.core.architecture.FeatureConfig as CoreFeatureConfig
import com.archguard.core.architecture.FeatureRuleConfig as CoreFeatureRuleConfig
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class FeatureRuleConfig @Inject constructor(
    val name: String,
) {
    private val parameters: MutableMap<String, String> = linkedMapOf()

    fun parameter(name: String, value: String) {
        parameters[name] = value
    }

    fun toCoreConfig(): CoreFeatureRuleConfig = CoreFeatureRuleConfig(
        name = name,
        parameters = parameters.toMap(),
    )
}

open class FeatureConfig @Inject constructor(
    private val objects: ObjectFactory,
    val name: String,
) {
    private val requiredLayers: MutableList<String> = mutableListOf()
    private val childFeatures: MutableList<FeatureConfig> = mutableListOf()
    private val forbiddenFolders: MutableSet<String> = linkedSetOf()
    private val customRules: MutableList<FeatureRuleConfig> = mutableListOf()

    @JvmSynthetic
    fun feature(name: String, action: Action<in FeatureConfig>) {
        val child = objects.newInstance(FeatureConfig::class.java, objects, name)
        action.execute(child)
        childFeatures += child
    }

    fun feature(name: String, closure: Closure<*>) {
        val child = objects.newInstance(FeatureConfig::class.java, objects, name)
        configureClosure(closure, child)
        childFeatures += child
    }

    fun requiredLayer(name: String) {
        requiredLayers += name
    }

    fun forbid(vararg names: String) {
        forbiddenFolders += names
    }

    fun customRule(name: String, action: Action<in FeatureRuleConfig>) {
        val rule = objects.newInstance(FeatureRuleConfig::class.java, name)
        action.execute(rule)
        customRules += rule
    }

    fun customRule(name: String, closure: Closure<*>) {
        val rule = objects.newInstance(FeatureRuleConfig::class.java, name)
        configureClosure(closure, rule)
        customRules += rule
    }

    fun hasContent(): Boolean {
        return requiredLayers.isNotEmpty() ||
            childFeatures.isNotEmpty() ||
            forbiddenFolders.isNotEmpty() ||
            customRules.isNotEmpty()
    }

    fun toCoreConfig(): CoreFeatureConfig = CoreFeatureConfig(
        name = name,
        requiredLayers = requiredLayers.toList(),
        childFeatures = childFeatures.map { it.toCoreConfig() },
        forbiddenFolders = forbiddenFolders.toSet(),
        customRules = customRules.map { it.toCoreConfig() },
    )
}
