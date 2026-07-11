package com.archguard.core.architecture

class FeatureStructureRule(
    private val config: ArchitectureConfig = ArchitectureConfig(),
) : Rule {
    override val id: String = "FeatureStructureRule"
    override val title: String = "Feature structure validation"
    override val description: String =
        "Validates that every feature folder contains presentation, domain, and data subfolders."

    fun inspect(project: FolderNode): List<FeatureValidation> = findFeatures(project)

    override fun evaluate(project: FolderNode): RuleResult {
        val requiredViolations = inspect(project).flatMap { feature ->
            feature.missingFolders.map { missingFolder ->
                Violation(
                    ruleId = id,
                    message = "Missing folder: $missingFolder",
                    location = feature.path.toString(),
                )
            }
        }
        val forbiddenViolations = findForbiddenFolders(project)
        return RuleResult(ruleId = id, violations = requiredViolations + forbiddenViolations)
    }

    private fun findFeatures(project: FolderNode): List<FeatureValidation> {
        val requiredFolders = config.requiredLayers
            .asSequence()
            .filter { it.required }
            .map { it.name }
            .toList()

        val features = project.descendantsAndSelf()
            .filter { it.name == config.featureRoot }
            .flatMap { featureContainer -> featureContainer.children.asSequence() }

        return features.map { feature ->
            val childNames = feature.children.map { it.name }.toSet()
            val missingFolders = requiredFolders.filterNot { it in childNames }
            FeatureValidation(
                name = feature.name,
                path = feature.path,
                missingFolders = missingFolders,
            )
        }.toList()
    }

    private fun findForbiddenFolders(project: FolderNode): List<Violation> {
        if (config.forbiddenFolderNames.isEmpty()) return emptyList()

        return project.descendantsAndSelf()
            .filter { it.name == config.featureRoot }
            .flatMap { featureContainer -> featureContainer.children.asSequence() }
            .flatMap { feature -> feature.descendantsAndSelf().asSequence().drop(1) }
            .filter { it.name in config.forbiddenFolderNames }
            .map { folder ->
                Violation(
                    ruleId = id,
                    message = "Forbidden folder: ${folder.name}",
                    location = folder.path.toString(),
                )
            }
            .toList()
    }
}
