package com.archguard.gradle.core.architecture

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RuleEngineTest {
    @Test
    fun `rule engine evaluates all rules and aggregates violations`() {
        val project = folderTree(
            "kotlin",
            folderTree(
                "feature",
                folderTree("login", folderTree("presentation"), folderTree("domain"), folderTree("data")),
                folderTree("profile", folderTree("presentation"), folderTree("data")),
            ),
        )

        val result = RuleEngine(listOf(FeatureStructureRule())).evaluate(project)

        assertEquals(1, result.ruleResults.size)
        assertEquals(1, result.violations.size)
        assertTrue(result.hasViolations)
    }

    @Test
    fun `rule engine reports clean projects without violations`() {
        val project = folderTree(
            "kotlin",
            folderTree(
                "feature",
                folderTree("login", folderTree("presentation"), folderTree("domain"), folderTree("data")),
            ),
        )

        val result = RuleEngine(listOf(FeatureStructureRule())).evaluate(project)

        assertFalse(result.hasViolations)
        assertTrue(result.violations.isEmpty())
    }

    private fun folderTree(name: String, vararg children: FolderNode): FolderNode {
        return FolderNode(
            name = name,
            path = Path.of(name),
            children = children.toList(),
        )
    }
}

