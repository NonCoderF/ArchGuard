package com.archguard.core.rules

import com.archguard.core.model.ProjectModel
import com.archguard.core.model.RuleResult

interface Rule {
    val id: String
    val title: String
    val description: String

    fun evaluate(project: ProjectModel): RuleResult
}
