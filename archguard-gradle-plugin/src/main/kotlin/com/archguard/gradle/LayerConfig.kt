package com.archguard.gradle

import com.archguard.core.architecture.LayerConfig as CoreLayerConfig
import javax.inject.Inject

open class LayerConfig @Inject constructor(
    val name: String,
) {
    var required: Boolean = false

    fun toCoreConfig(): CoreLayerConfig = CoreLayerConfig(
        name = name,
        required = required,
    )
}
