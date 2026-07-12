package com.archguard.gradle

import com.archguard.core.architecture.ArchitectureConfig as CoreArchitectureConfig
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class ArchGuardExtension @Inject constructor(
    private val objects: ObjectFactory,
) {
    var failOnViolation: Boolean = true
    var ignoredPackages: List<String> = emptyList()

    private val reportsConfig: ReportConfig = objects.newInstance(ReportConfig::class.java, objects)
    private val architectureConfig: ArchitectureConfig = objects.newInstance(ArchitectureConfig::class.java, objects)

    fun reports(action: Action<in ReportConfig>) {
        action.execute(reportsConfig)
    }

    fun reports(closure: Closure<*>) {
        configureClosure(closure, reportsConfig)
    }

    fun architecture(action: Action<in ArchitectureConfig>) {
        action.execute(architectureConfig)
    }

    fun architecture(closure: Closure<*>) {
        configureClosure(closure, architectureConfig)
    }

    fun architectureConfig(): CoreArchitectureConfig = architectureConfig.toCoreConfig()

    fun htmlReportEnabled(): Boolean = reportsConfig.htmlReportEnabled()

    fun htmlReportPath(): String = reportsConfig.htmlReportPath()
}
