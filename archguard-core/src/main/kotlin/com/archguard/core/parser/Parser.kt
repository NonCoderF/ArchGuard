package com.archguard.core.parser

import com.archguard.core.model.ProjectModel
import com.archguard.core.model.SourceFile

/** Contract for transforming scanned source inputs into architecture models. */
interface Parser {
    fun parse(sourceFiles: List<SourceFile>): ProjectModel
}
