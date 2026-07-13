package com.archguard.gradle.core.architecture

import java.nio.file.Path

data class FolderNode(
    val name: String,
    val path: Path,
    val children: List<FolderNode>,
) {
    fun child(name: String): FolderNode? = children.firstOrNull { it.name == name }

    fun descendantsAndSelf(): Sequence<FolderNode> = sequence {
        yield(this@FolderNode)
        for (child in children) {
            yieldAll(child.descendantsAndSelf())
        }
    }
}

