package com.archguard.gradle.core.architecture

import java.nio.file.Files
import java.nio.file.Path

class FolderScanner(
    private val sourceDirectory: Path = Path.of("src", "main", "kotlin"),
    private val ignoredFolderNames: Set<String> = setOf("build", ".gradle", ".idea"),
) {
    fun scan(projectRoot: Path): FolderNode {
        val sourceRoot = projectRoot.resolve(sourceDirectory).toAbsolutePath().normalize()
        require(Files.isDirectory(sourceRoot)) {
            "Source directory does not exist: $sourceRoot"
        }
        return readNode(sourceRoot)
    }

    private fun readNode(path: Path): FolderNode {
        val children = Files.list(path).use { stream ->
            stream.filter(Files::isDirectory)
                .filter { !it.isIgnoredFolder() }
                .sorted(compareBy { it.fileName.toString().lowercase() })
                .map { readNode(it) }
                .toList()
        }

        return FolderNode(
            name = path.fileName?.toString() ?: path.toString(),
            path = path,
            children = children,
        )
    }

    private fun Path.isIgnoredFolder(): Boolean {
        val folderName = fileName?.toString() ?: return false
        if (folderName.startsWith(".")) {
            return true
        }
        return ignoredFolderNames.any { it == folderName }
    }
}

