package com.archguard.core.graph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DependencyGraphTest {
    @Test
    fun `addEdge returns a new graph and preserves immutability`() {
        val graph = DependencyGraph()
        val updated = graph.addEdge("a", "b")

        assertTrue(updated.edges.isNotEmpty())
        assertTrue(graph.edges.isEmpty())
        assertEquals(setOf("a", "b"), updated.nodes)
        assertEquals(setOf("b"), updated.outgoingDependenciesOf("a"))
        assertEquals(setOf("a"), updated.incomingDependenciesOf("b"))
    }

    @Test
    fun `hasCycle detects simple cycle`() {
        val graph = DependencyGraph(
            edges = setOf(
                com.archguard.core.model.DependencyEdge("a", "b"),
                com.archguard.core.model.DependencyEdge("b", "c"),
                com.archguard.core.model.DependencyEdge("c", "a"),
            ),
        )

        assertTrue(graph.hasCycle())
        assertEquals(listOf("a", "b", "c", "a"), graph.findCycle())
    }

    @Test
    fun `hasCycle returns false for acyclic graph`() {
        val graph = DependencyGraph(
            edges = setOf(
                com.archguard.core.model.DependencyEdge("a", "b"),
                com.archguard.core.model.DependencyEdge("b", "c"),
            ),
        )

        assertFalse(graph.hasCycle())
        assertEquals(null, graph.findCycle())
    }
}
