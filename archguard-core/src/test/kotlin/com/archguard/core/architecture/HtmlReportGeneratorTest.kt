package com.archguard.core.architecture

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class HtmlReportGeneratorTest {
    @Test
    fun `render produces a styled html document`() {
        val result = ArchitectureAnalysisResult(
            projectRoot = Path.of("sample-project"),
            sourceRoot = FolderNode(
                name = "kotlin",
                path = Path.of("sample-project/src/main/kotlin"),
                children = emptyList(),
            ),
            featureResults = listOf(
                FeatureValidation(
                    name = "login",
                    path = Path.of("sample-project/src/main/kotlin/feature/login"),
                    missingFolders = emptyList(),
                ),
                FeatureValidation(
                    name = "profile",
                    path = Path.of("sample-project/src/main/kotlin/feature/profile"),
                    missingFolders = listOf("domain"),
                ),
            ),
            ruleEngineResult = RuleEngineResult(
                ruleResults = listOf(
                    RuleResult(
                        ruleId = "FeatureStructureRule",
                        violations = listOf(
                            Violation(
                                ruleId = "FeatureStructureRule",
                                message = "Missing folder: domain",
                                location = "sample-project/src/main/kotlin/feature/profile",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val html = HtmlReportGenerator().render(result)

        assertTrue(html.startsWith("<!doctype html>"))
        assertTrue(html.contains("<title>ArchGuard Report</title>"))
        assertTrue(html.contains("Features Found"))
        assertTrue(html.contains("profile"))
        assertTrue(html.contains("Missing folders"))
    }
}
