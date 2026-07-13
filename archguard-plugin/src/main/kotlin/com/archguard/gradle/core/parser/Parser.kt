package com.archguard.gradle.core.parser

import com.archguard.gradle.core.model.ProjectModel
import com.archguard.gradle.core.model.SourceFile

/** Contract for transforming scanned source inputs into architecture models. */
interface Parser {
    fun parse(sourceFiles: List<SourceFile>): ProjectModel
}

