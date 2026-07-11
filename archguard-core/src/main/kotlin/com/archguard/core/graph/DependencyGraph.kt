package com.archguard.core.graph

import com.archguard.core.model.DependencyEdge

data class DependencyGraph(
    val edges: Set<DependencyEdge> = emptySet(),
) {
    val nodes: Set<String>
        get() = buildSet {
            edges.forEach { edge ->
                add(edge.from)
                add(edge.to)
            }
        }

    fun addEdge(from: String, to: String): DependencyGraph =
        copy(edges = edges + DependencyEdge(from = from, to = to))

    fun addEdge(edge: DependencyEdge): DependencyGraph =
        copy(edges = edges + edge)

    fun outgoingDependenciesOf(node: String): Set<String> =
        edges.asSequence()
            .filter { it.from == node }
            .map { it.to }
            .toSet()

    fun incomingDependenciesOf(node: String): Set<String> =
        edges.asSequence()
            .filter { it.to == node }
            .map { it.from }
            .toSet()

    fun hasCycle(): Boolean = findCycle() != null

    fun findCycle(): List<String>? {
        val adjacency = edges.groupBy({ it.from }, { it.to })
        val visiting = linkedSetOf<String>()
        val visited = mutableSetOf<String>()

        fun visit(node: String, path: MutableList<String>): List<String>? {
            if (node in visiting) {
                val startIndex = path.indexOf(node)
                return if (startIndex >= 0) path.subList(startIndex, path.size).toList() + node else listOf(node)
            }
            if (node in visited) return null

            visiting.add(node)
            path.add(node)

            for (next in adjacency[node].orEmpty()) {
                val cycle = visit(next, path)
                if (cycle != null) return cycle
            }

            path.removeAt(path.lastIndex)
            visiting.remove(node)
            visited.add(node)
            return null
        }

        for (node in nodes) {
            val cycle = visit(node, mutableListOf())
            if (cycle != null) return cycle
        }

        return null
    }
}
