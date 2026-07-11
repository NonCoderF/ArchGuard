package com.archguard.core.parser

import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectScannerTest {
    private val scanner = ProjectScanner()

    @Test
    fun `scan returns only kotlin files`() {
        val root = Files.createTempDirectory("archguard-scanner")
        root.resolve("src/main/kotlin/com/example").createDirectories()
        root.resolve("src/main/kotlin/com/example/Sample.kt").writeText(
            """
            package com.example

            import kotlin.collections.List

            @Deprecated("legacy")
            class Sample

            interface Worker

            fun topLevelFunction() = Unit
            """.trimIndent(),
        )
        root.resolve("src/main/java/com/example").createDirectories()
        root.resolve("src/main/java/com/example/Ignore.java").writeText("class Ignore {}")

        val result = scanner.scan(root)

        assertEquals(1, result.size)
        assertEquals(root.resolve("src/main/kotlin/com/example/Sample.kt").toAbsolutePath().normalize(), result.single().path)
        assertEquals("com.example", result.single().packageModel?.name)
        assertEquals(listOf("kotlin.collections.List"), result.single().imports.map { it.qualifiedName })
        assertEquals(listOf("Sample"), result.single().classes.map { it.name })
        assertEquals(listOf("Worker"), result.single().interfaces.map { it.name })
        assertEquals(listOf("topLevelFunction"), result.single().functions.map { it.name })
        assertEquals(listOf("Deprecated"), result.single().annotations.map { it.name })
    }

    @Test
    fun `scan ignores generated directories and keeps results sorted`() {
        val root = Files.createTempDirectory("archguard-scanner-order")
        root.resolve("b/src/main/kotlin").createDirectories()
        root.resolve("b/src/main/kotlin/B.kt").writeText("package demo\nclass B")
        root.resolve("a/src/main/kotlin").createDirectories()
        root.resolve("a/src/main/kotlin/A.kt").writeText("package demo\nclass A")
        root.resolve("build/generated/kotlin").createDirectories()
        root.resolve("build/generated/kotlin/Generated.kt").writeText("package demo\nclass Generated")

        val result = scanner.scan(root)

        assertEquals(2, result.size)
        assertTrue(result[0].path.toString().endsWith("A.kt"))
        assertTrue(result[1].path.toString().endsWith("B.kt"))
        assertFalse(result.any { it.path.toString().contains("Generated.kt") })
    }
}
