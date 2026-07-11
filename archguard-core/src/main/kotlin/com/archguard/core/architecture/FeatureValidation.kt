package com.archguard.core.architecture

import java.nio.file.Path

data class FeatureValidation(
    val name: String,
    val path: Path,
    val missingFolders: List<String>,
) {
    val isPassed: Boolean
        get() = missingFolders.isEmpty()
}
