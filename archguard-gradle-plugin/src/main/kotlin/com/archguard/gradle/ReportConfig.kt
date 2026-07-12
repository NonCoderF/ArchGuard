package com.archguard.gradle

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class ReportConfig @Inject constructor(
    private val objects: ObjectFactory,
) {
    private val htmlConfig: HtmlReportConfig = objects.newInstance(HtmlReportConfig::class.java)

    @JvmSynthetic
    fun html(action: Action<in HtmlReportConfig>) {
        action.execute(htmlConfig)
    }

    fun html(closure: Closure<*>) {
        configureClosure(closure, htmlConfig)
    }

    fun htmlReportEnabled(): Boolean = htmlConfig.enabled

    fun htmlReportPath(): String = htmlConfig.outputPath
}
