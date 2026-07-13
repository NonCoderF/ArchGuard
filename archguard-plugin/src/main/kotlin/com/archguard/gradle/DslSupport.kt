package com.archguard.gradle

import groovy.lang.Closure

internal fun <T> configureClosure(closure: Closure<*>, target: T): T {
    val configured = closure.rehydrate(target, closure.owner, closure.thisObject)
    configured.resolveStrategy = Closure.DELEGATE_FIRST
    configured.call()
    return target
}
