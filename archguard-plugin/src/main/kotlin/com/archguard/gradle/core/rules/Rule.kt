package com.archguard.gradle.core.rules

import com.archguard.gradle.core.model.ProjectModel
import com.archguard.gradle.core.model.RuleResult

interface Rule {
    val id: String
    val title: String
    val description: String

    fun evaluate(project: ProjectModel): RuleResult
}

