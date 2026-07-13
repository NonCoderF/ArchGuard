package com.archguard.gradle.core.rules

import com.archguard.gradle.core.graph.DependencyGraph
import com.archguard.gradle.core.model.DependencyEdge
import com.archguard.gradle.core.model.ProjectModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RuleEngineTest {
    @Test
    fun `rule engine evaluates every rule`() {
        val project = ProjectModel(
            dependencyGraph = DependencyGraph(
                edges = setOf(
                    DependencyEdge("a", "b"),
                    DependencyEdge("b", "c"),
                ),
            ),
        )

        val result = RuleEngine(listOf(NoCircularDependencyRule())).evaluate(project)

        assertEquals(1, result.ruleResults.size)
        assertTrue(result.ruleResults.single().violations.isEmpty())
        assertFalse(result.hasViolations)
    }

    @Test
    fun `rule engine aggregates violations from cyclic graphs`() {
        val project = ProjectModel(
            dependencyGraph = DependencyGraph(
                edges = setOf(
                    DependencyEdge("a", "b"),
                    DependencyEdge("b", "a"),
                ),
            ),
        )

        val result = RuleEngine(listOf(NoCircularDependencyRule())).evaluate(project)

        assertTrue(result.hasViolations)
        assertEquals(1, result.violations.size)
        assertTrue(result.violations.single().message.contains("Circular dependency detected"))
    }
}

