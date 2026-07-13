package com.archguard.gradle

import javax.inject.Inject

/**
 * Legacy compatibility object for root-level `layer("...") { }` calls.
 * The feature-centric DSL should prefer `requiredLayer("...")` inside `feature { }`.
 */
open class LayerConfig @Inject constructor(
    val name: String,
) {
    var required: Boolean = false
}
