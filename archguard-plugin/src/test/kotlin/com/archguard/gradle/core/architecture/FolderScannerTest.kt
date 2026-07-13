package com.archguard.gradle.core.architecture

import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FolderScannerTest {
    private val scanner = FolderScanner()

    @Test
    fun `scan builds an immutable folder tree for source root`() {
        val projectRoot = Files.createTempDirectory("archguard-folder-scanner")
        projectRoot.resolve("src/main/kotlin/feature/login/presentation").createDirectories()
        projectRoot.resolve("src/main/kotlin/feature/login/domain").createDirectories()
        projectRoot.resolve("src/main/kotlin/feature/login/data").createDirectories()
        projectRoot.resolve("src/main/kotlin/build/generated").createDirectories()
        projectRoot.resolve("src/main/kotlin/.idea/workspace").createDirectories()
        projectRoot.resolve("src/main/kotlin/feature/login/.hidden").createDirectories()
        projectRoot.resolve("src/main/kotlin/feature/login/presentation/LoginPresentation.kt").writeText("object LoginPresentation")

        val root = scanner.scan(projectRoot)

        assertEquals("kotlin", root.name)
        assertEquals(listOf("feature"), root.children.map { it.name })

        val feature = root.child("feature")
        requireNotNull(feature)
        assertEquals(listOf("login"), feature.children.map { it.name })

        val login = feature.child("login")
        requireNotNull(login)
        assertEquals(listOf("data", "domain", "presentation"), login.children.map { it.name })
        assertFalse(root.descendantsAndSelf().any { it.name == "build" })
        assertFalse(root.descendantsAndSelf().any { it.name == ".idea" })
        assertFalse(root.descendantsAndSelf().any { it.name == ".hidden" })
        assertTrue(login.path.toString().endsWith("feature\\login") || login.path.toString().endsWith("feature/login"))
    }
}

