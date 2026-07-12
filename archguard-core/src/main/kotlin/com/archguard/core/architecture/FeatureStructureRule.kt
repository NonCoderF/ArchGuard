package com.archguard.core.architecture

import java.nio.file.Path

class FeatureStructureRule(
    private val config: ArchitectureConfig = ArchitectureConfig(),
) : Rule {
    override val id: String = "FeatureStructureRule"
    override val title: String = "Feature structure validation"
    override val description: String =
        "Validates feature trees against required layers, nested features, and forbidden folders."

    fun inspect(project: FolderNode): List<FeatureValidation> = findFeatures(project)

    override fun evaluate(project: FolderNode): RuleResult {
        val violations = inspect(project).flatMap { feature -> feature.allViolations(id) }
        return RuleResult(ruleId = id, violations = violations)
    }

    private fun findFeatures(project: FolderNode): List<FeatureValidation> {
        val featureContainer = project.descendantsAndSelf().firstOrNull { it.name == config.featureRoot }
            ?: return emptyList()

        val featureConfigs = config.features
        val templateConfigs = featureConfigs.filter { it.isAnonymousTemplate }
        val namedConfigs = featureConfigs.filterNot { it.isAnonymousTemplate }

        val namedResults = namedConfigs.map { featureConfig ->
            validateFeature(featureContainer, featureConfig)
        }

        val templateResults = templateConfigs.flatMap { templateConfig ->
            featureContainer.children.map { childFeature ->
                validateTemplateFeature(childFeature, templateConfig)
            }
        }

        return namedResults + templateResults
    }

    private fun validateFeature(parentFolder: FolderNode, featureConfig: FeatureConfig): FeatureValidation {
        val actualFolder = parentFolder.child(featureConfig.name)
        val expectedFolder = actualFolder ?: expectedFolder(parentFolder.path, featureConfig.name)

        return validateNode(actualFolder ?: expectedFolder, featureConfig)
    }

    private fun validateTemplateFeature(featureFolder: FolderNode, featureConfig: FeatureConfig): FeatureValidation {
        return validateNode(featureFolder, featureConfig, displayName = featureFolder.name)
    }

    private fun validateNode(
        featureFolder: FolderNode,
        featureConfig: FeatureConfig,
        displayName: String = featureConfig.name,
    ): FeatureValidation {
        val layerStatuses = featureConfig.requiredLayers.map { layerName ->
            LayerValidation(
                name = layerName,
                present = featureFolder.child(layerName) != null,
            )
        }

        val childFeatures = featureConfig.childFeatures.map { childConfig ->
            validateChildFeature(featureFolder, childConfig)
        }

        val violations = findForbiddenFolders(featureFolder, featureConfig.forbiddenFolders)

        return FeatureValidation(
            name = displayName.ifBlank { featureFolder.name },
            path = featureFolder.path,
            requiredLayers = layerStatuses,
            childFeatures = childFeatures,
            violations = violations,
        )
    }

    private fun validateChildFeature(parentFolder: FolderNode, featureConfig: FeatureConfig): FeatureValidation {
        val actualFolder = parentFolder.child(featureConfig.name)
        val expected = actualFolder ?: expectedFolder(parentFolder.path, featureConfig.name)
        return validateNode(actualFolder ?: expected, featureConfig)
    }

    private fun findForbiddenFolders(project: FolderNode, forbiddenFolders: Set<String>): List<Violation> {
        if (forbiddenFolders.isEmpty()) return emptyList()

        return project.descendantsAndSelf()
            .drop(1)
            .filter { it.name in forbiddenFolders }
            .map { folder ->
                Violation(
                    ruleId = id,
                    message = "Forbidden folder: ${folder.name}",
                    location = folder.path.toString(),
                )
            }
            .toList()
    }

    private fun expectedFolder(parentPath: Path, name: String): FolderNode {
        return FolderNode(
            name = name,
            path = parentPath.resolve(name),
            children = emptyList(),
        )
    }
}
