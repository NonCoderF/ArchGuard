package com.archguard.gradle.core.model

import java.nio.file.Path

data class SourceFile(
    val path: Path,
    val packageModel: PackageModel?,
    val imports: List<ImportModel> = emptyList(),
    val classes: List<ClassModel> = emptyList(),
    val interfaces: List<InterfaceModel> = emptyList(),
    val functions: List<FunctionModel> = emptyList(),
    val annotations: List<AnnotationModel> = emptyList(),
    val content: String = "",
)

