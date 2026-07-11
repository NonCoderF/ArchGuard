package com.archguard.gradle

import com.archguard.core.architecture.ArchitectureConfig
import com.archguard.core.architecture.LayerConfig

open class ArchGuardExtension {
    var failOnViolation: Boolean = true
    var ignoredPackages: List<String> = emptyList()

    private val reportsDsl: ReportsDsl = ReportsDsl()
    private val architecture = ArchitectureDsl()

    fun architecture(block: ArchitectureDsl.() -> Unit) {
        architecture.block()
    }

    fun reports(block: ReportsDsl.() -> Unit) {
        reportsDsl.block()
    }

    fun architectureConfig(): ArchitectureConfig = architecture.toConfig()

    fun htmlReportEnabled(): Boolean = reportsDsl.html.enabled

    fun htmlReportPath(): String = reportsDsl.html.outputPath
}

class ReportsDsl {
    val html: HtmlReportDsl = HtmlReportDsl()

    fun html(block: HtmlReportDsl.() -> Unit) {
        html.block()
    }
}

class HtmlReportDsl {
    var enabled: Boolean = false
    var outputPath: String = "archguard-report.html"
}

class ArchitectureDsl {
    var featureRoot: String = "feature"

    private val layers = linkedMapOf<String, LayerDsl>()
    private val forbiddenFolderNames = linkedSetOf<String>()

    fun layer(name: String, block: LayerDsl.() -> Unit) {
        val layer = layers.getOrPut(name) { LayerDsl(name) }
        layer.block()
    }

    fun forbid(vararg names: String) {
        names.asSequence()
            .filter { it.isNotBlank() }
            .forEach { forbiddenFolderNames.add(it) }
    }

    fun toConfig(): ArchitectureConfig {
        val requiredLayers = if (layers.isEmpty()) {
            listOf(
                LayerConfig(name = "presentation", required = true),
                LayerConfig(name = "domain", required = true),
                LayerConfig(name = "data", required = true),
            )
        } else {
            layers.values.map { it.toConfig() }
        }

        return ArchitectureConfig(
            featureRoot = featureRoot,
            requiredLayers = requiredLayers,
            forbiddenFolderNames = forbiddenFolderNames.ifEmpty {
                linkedSetOf("helper", "util", "manager")
            },
        )
    }
}

class LayerDsl(
    private val name: String,
) {
    var required: Boolean = false

    fun toConfig(): LayerConfig = LayerConfig(name = name, required = required)
}
