package com.archguard.core.architecture

import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class HtmlReportGeneratorTest {
    @Test
    fun `render produces a hierarchical html report`() {
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
                    requiredLayers = listOf(
                        LayerValidation("presentation", true),
                        LayerValidation("domain", true),
                        LayerValidation("data", true),
                    ),
                    childFeatures = listOf(
                        FeatureValidation(
                            name = "chat",
                            path = Path.of("sample-project/src/main/kotlin/feature/login/chat"),
                            requiredLayers = listOf(
                                LayerValidation("ui", true),
                                LayerValidation("socket", false),
                            ),
                            childFeatures = listOf(
                                FeatureValidation(
                                    name = "voice",
                                    path = Path.of("sample-project/src/main/kotlin/feature/login/chat/voice"),
                                    requiredLayers = listOf(
                                        LayerValidation("recording", true),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
            ruleEngineResult = RuleEngineResult(
                ruleResults = listOf(
                    RuleResult(
                        ruleId = "FeatureStructureRule",
                        violations = listOf(
                            Violation(
                                ruleId = "FeatureStructureRule",
                                message = "Missing folder: socket",
                                location = "sample-project/src/main/kotlin/feature/login/chat",
                            ),
                        ),
                    ),
                ),
            ),
        )

        val html = HtmlReportGenerator().render(result)

        assertTrue(html.startsWith("<!doctype html>"))
        assertTrue(html.contains("<title>ArchGuard Report</title>"))
        assertTrue(html.contains("Feature Tree"))
        assertTrue(html.contains("login"))
        assertTrue(html.contains("chat"))
        assertTrue(html.contains("voice"))
        assertTrue(html.contains("PASS"))
        assertTrue(html.contains("FAIL"))
        assertTrue(html.contains("Missing folder: socket"))
    }
}
