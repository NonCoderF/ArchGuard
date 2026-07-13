package com.archguard.gradle.core.parser

import com.archguard.gradle.core.model.AnnotationModel
import com.archguard.gradle.core.model.ClassModel
import com.archguard.gradle.core.model.FunctionModel
import com.archguard.gradle.core.model.ImportModel
import com.archguard.gradle.core.model.InterfaceModel
import com.archguard.gradle.core.model.PackageModel
import com.archguard.gradle.core.model.SourceFile
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class ProjectScanner(
    private val excludedDirectories: Set<String> = setOf(".git", ".gradle", "build", "out"),
) {
    fun scan(root: Path): List<SourceFile> {
        require(Files.exists(root)) { "Scan root does not exist: $root" }

        val scanRoot = root.toAbsolutePath().normalize()
        val files = if (Files.isRegularFile(scanRoot)) {
            if (scanRoot.isKotlinSource()) listOf(scanRoot) else emptyList()
        } else {
            Files.walk(scanRoot).use { paths ->
                paths.filter(Files::isRegularFile)
                    .filter { it.isKotlinSource() }
                    .filter { !it.containsExcludedDirectory() }
                    .sorted()
                    .collect(Collectors.toList())
            }
        }

        return files.map { parseSourceFile(it) }
    }

    private fun Path.isKotlinSource(): Boolean = fileName?.toString()?.endsWith(".kt") == true

    private fun Path.containsExcludedDirectory(): Boolean {
        var current: Path? = parent
        while (current != null) {
            val segment = current.fileName?.toString()?.lowercase()
            if (segment != null && excludedDirectories.any { it.lowercase() == segment }) {
                return true
            }
            current = current.parent
        }
        return false
    }

    private fun parseSourceFile(path: Path): SourceFile {
        val content = readContent(path)
        val lines = content.lineSequence().toList()

        val packageModel = lines
            .firstNotNullOfOrNull { parsePackageLine(it) }
            ?.let { PackageModel(it) }

        val imports = lines.mapNotNull { parseImportLine(it) }.map { ImportModel(it) }
        val classes = lines.mapNotNull { parseClassLine(it, packageModel?.name).firstOrNull() }
        val interfaces = lines.mapNotNull { parseInterfaceLine(it, packageModel?.name).firstOrNull() }
        val functions = lines.mapNotNull { parseFunctionLine(it) }.map { FunctionModel(it) }
        val annotations = lines.mapNotNull { parseAnnotationLine(it) }.map { AnnotationModel(it) }

        return SourceFile(
            path = path.toAbsolutePath().normalize(),
            packageModel = packageModel,
            imports = imports,
            classes = classes,
            interfaces = interfaces,
            functions = functions,
            annotations = annotations,
            content = content,
        )
    }

    private fun readContent(path: Path): String =
        try {
            Files.readString(path, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            throw IllegalStateException("Failed to read Kotlin source file: $path", ex)
        }

    private fun parsePackageLine(line: String): String? {
        val trimmed = line.trim()
        if (!trimmed.startsWith("package ")) return null
        return trimmed.removePrefix("package ").trim().ifBlank { null }
    }

    private fun parseImportLine(line: String): String? {
        val trimmed = line.trim()
        if (!trimmed.startsWith("import ")) return null
        return trimmed.removePrefix("import ").trim().ifBlank { null }
    }

    private fun parseClassLine(line: String, packageName: String?): List<ClassModel> {
        val trimmed = line.trim()
        val keywordIndex = trimmed.indexOf("class ")
        if (keywordIndex < 0) return emptyList()
        val name = trimmed.substring(keywordIndex + "class ".length)
            .takeWhile { !it.isWhitespace() && it != '(' && it != ':' && it != '<' && it != '{' }
        if (name.isBlank()) return emptyList()
        return listOf(ClassModel(name = name, packageName = packageName.orEmpty()))
    }

    private fun parseInterfaceLine(line: String, packageName: String?): List<InterfaceModel> {
        val trimmed = line.trim()
        val keywordIndex = trimmed.indexOf("interface ")
        if (keywordIndex < 0) return emptyList()
        val name = trimmed.substring(keywordIndex + "interface ".length)
            .takeWhile { !it.isWhitespace() && it != '(' && it != ':' && it != '<' && it != '{' }
        if (name.isBlank()) return emptyList()
        return listOf(InterfaceModel(name = name, packageName = packageName.orEmpty()))
    }

    private fun parseFunctionLine(line: String): String? {
        val trimmed = line.trim()
        val functionIndex = trimmed.indexOf("fun ")
        if (functionIndex < 0) return null
        val prefix = trimmed.substring(0, functionIndex)
        val allowedPrefixes = setOf(
            "",
            "public ",
            "private ",
            "protected ",
            "internal ",
            "override ",
            "suspend ",
            "inline ",
            "tailrec ",
            "operator ",
            "infix ",
            "external ",
            "expect ",
            "actual ",
        )
        if (prefix.isNotBlank() && allowedPrefixes.none { prefix.endsWith(it) }) return null

        return trimmed.substring(functionIndex + "fun ".length)
            .takeWhile { !it.isWhitespace() && it != '(' && it != '<' && it != ':' }
            .ifBlank { null }
    }

    private fun parseAnnotationLine(line: String): String? {
        val trimmed = line.trim()
        if (!trimmed.startsWith("@")) return null
        val candidate = trimmed.removePrefix("@")
            .takeWhile { !it.isWhitespace() && it != '(' }
        return candidate.ifBlank { null }
    }
}

