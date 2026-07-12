package com.archguard.core.architecture

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeatureStructureRuleTest {
    @Test
    fun `single feature passes when required layers exist`() {
        val rule = FeatureStructureRule(config = configFor(
            feature(
                name = "login",
                requiredLayers = listOf("presentation", "domain", "data"),
            ),
        ))

        val result = rule.inspect(projectTree(
            featureFolder(
                "login",
                featureFolder("presentation"),
                featureFolder("domain"),
                featureFolder("data"),
            ),
        ))

        assertEquals(1, result.size)
        assertTrue(result.single().isPassed)
        assertTrue(rule.evaluate(projectTree(featureFolder("login", featureFolder("presentation"), featureFolder("domain"), featureFolder("data")))).violations.isEmpty())
    }

    @Test
    fun `nested feature passes when child layers exist`() {
        val rule = FeatureStructureRule(config = configFor(
            feature(
                name = "login",
                requiredLayers = listOf("presentation", "domain", "data"),
                childFeatures = listOf(
                    feature(
                        name = "chat",
                        requiredLayers = listOf("ui", "socket"),
                        childFeatures = listOf(
                            feature(
                                name = "voice",
                                requiredLayers = listOf("recording"),
                            ),
                        ),
                    ),
                ),
            ),
        ))

        val result = rule.inspect(projectTree(
            featureFolder(
                "login",
                featureFolder("presentation"),
                featureFolder("domain"),
                featureFolder("data"),
                featureFolder(
                    "chat",
                    featureFolder("ui"),
                    featureFolder("socket"),
                    featureFolder(
                        "voice",
                        featureFolder("recording"),
                    ),
                ),
            ),
        ))

        assertEquals(1, result.size)
        assertTrue(result.single().isPassed)
        assertEquals(3, result.single().flatten().count())
    }

    @Test
    fun `multiple nested levels report missing layers`() {
        val rule = FeatureStructureRule(config = configFor(
            feature(
                name = "login",
                requiredLayers = listOf("presentation", "domain", "data"),
                childFeatures = listOf(
                    feature(
                        name = "chat",
                        requiredLayers = listOf("ui", "socket"),
                        childFeatures = listOf(
                            feature(
                                name = "voice",
                                requiredLayers = listOf("recording"),
                            ),
                        ),
                    ),
                ),
            ),
        ))

        val result = rule.inspect(projectTree(
            featureFolder(
                "login",
                featureFolder("presentation"),
                featureFolder("domain"),
                featureFolder("data"),
                featureFolder(
                    "chat",
                    featureFolder("ui"),
                    featureFolder(
                        "voice",
                    ),
                ),
            ),
        ))

        val login = result.single()
        val chat = login.childFeatures.single()
        val voice = chat.childFeatures.single()

        assertFalse(login.isPassed)
        assertEquals(listOf("socket"), chat.missingFolders)
        assertEquals(listOf("recording"), voice.missingFolders)
    }

    @Test
    fun `forbidden folders produce violations`() {
        val rule = FeatureStructureRule(config = configFor(
            feature(
                name = "login",
                requiredLayers = listOf("presentation", "domain", "data"),
                forbiddenFolders = setOf("helper", "util", "manager"),
            ),
        ))

        val result = rule.evaluate(projectTree(
            featureFolder(
                "login",
                featureFolder("presentation"),
                featureFolder("domain"),
                featureFolder("data"),
                featureFolder("util"),
            ),
        ))

        assertEquals(1, result.violations.size)
        assertEquals("Forbidden folder: util", result.violations.single().message)
    }

    @Test
    fun `backward compatible anonymous feature still works`() {
        val rule = FeatureStructureRule()

        val result = rule.inspect(projectTree(
            featureFolder(
                "login",
                featureFolder("presentation"),
                featureFolder("domain"),
                featureFolder("data"),
            ),
            featureFolder(
                "profile",
                featureFolder("presentation"),
                featureFolder("data"),
            ),
        ))

        assertEquals(2, result.size)
        assertTrue(result.first().isPassed)
        assertFalse(result.last().isPassed)
    }

    private fun configFor(vararg features: FeatureConfig): ArchitectureConfig {
        return ArchitectureConfig(
            featureRoot = "feature",
            features = features.toList(),
        )
    }

    private fun feature(
        name: String,
        requiredLayers: List<String> = emptyList(),
        childFeatures: List<FeatureConfig> = emptyList(),
        forbiddenFolders: Set<String> = emptySet(),
    ): FeatureConfig {
        return FeatureConfig(
            name = name,
            requiredLayers = requiredLayers,
            childFeatures = childFeatures,
            forbiddenFolders = forbiddenFolders,
        )
    }

    private fun projectTree(vararg features: FolderNode): FolderNode {
        return folderTree(
            "kotlin",
            folderTree(
                "feature",
                *features,
            ),
        )
    }

    private fun featureFolder(name: String, vararg children: FolderNode): FolderNode {
        return folderTree(name, *children)
    }

    private fun folderTree(name: String, vararg children: FolderNode): FolderNode {
        return FolderNode(
            name = name,
            path = Path.of(name),
            children = children.toList(),
        )
    }
}
