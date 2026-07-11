package com.archguard.core.architecture

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureStructureRuleTest {
    private val rule = FeatureStructureRule()

    @Test
    fun `rule passes when all required folders exist`() {
        val project = folderTree(
            "kotlin",
            folderTree(
                "feature",
                folderTree("login", folderTree("presentation"), folderTree("domain"), folderTree("data")),
            ),
        )

        val result = rule.evaluate(project)

        assertTrue(result.violations.isEmpty())
        assertEquals(1, rule.inspect(project).size)
        assertTrue(rule.inspect(project).single().isPassed)
    }

    @Test
    fun `rule reports missing domain folder`() {
        val project = folderTree(
            "kotlin",
            folderTree(
                "feature",
                folderTree("profile", folderTree("presentation"), folderTree("data")),
            ),
        )

        val result = rule.evaluate(project)
        val inspection = rule.inspect(project)

        assertEquals(1, result.violations.size)
        assertEquals("Missing folder: domain", result.violations.single().message)
        assertEquals("profile", inspection.single().name)
        assertEquals(listOf("domain"), inspection.single().missingFolders)
    }

    @Test
    fun `rule reports forbidden folders`() {
        val project = folderTree(
            "kotlin",
            folderTree(
                "feature",
                folderTree("payment", folderTree("presentation"), folderTree("domain"), folderTree("data"), folderTree("util")),
            ),
        )

        val result = rule.evaluate(project)

        assertEquals(1, result.violations.size)
        assertEquals("Forbidden folder: util", result.violations.single().message)
    }

    private fun folderTree(name: String, vararg children: FolderNode): FolderNode {
        return FolderNode(
            name = name,
            path = Path.of(name),
            children = children.toList(),
        )
    }
}
