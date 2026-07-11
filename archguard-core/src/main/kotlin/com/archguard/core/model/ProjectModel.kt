package com.archguard.core.model

import com.archguard.core.graph.DependencyGraph

data class ProjectModel(
    val sourceFiles: List<SourceFile> = emptyList(),
    val modules: List<ModuleModel> = emptyList(),
    val dependencyGraph: DependencyGraph = DependencyGraph(),
) {
    val dependencyEdges: Set<DependencyEdge>
        get() = dependencyGraph.edges
}
