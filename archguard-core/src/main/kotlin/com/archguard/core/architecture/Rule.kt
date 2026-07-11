package com.archguard.core.architecture

interface Rule {
    val id: String

    val title: String

    val description: String

    fun evaluate(project: FolderNode): RuleResult
}
