package com.archguard.core.architecture

class HtmlReportGenerator {
    fun render(result: ArchitectureAnalysisResult): String {
        val passed = result.passedFeatures
        val failed = result.failedFeatures
        val totalViolations = result.violations.size

        return buildString {
            appendLine("<!doctype html>")
            appendLine("<html lang=\"en\">")
            appendLine("<head>")
            appendLine("  <meta charset=\"utf-8\">")
            appendLine("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
            appendLine("  <title>ArchGuard Report</title>")
            appendLine("  <style>")
            appendLine("    :root {")
            appendLine("      color-scheme: light;")
            appendLine("      --bg: #f5f7fb;")
            appendLine("      --panel: #ffffff;")
            appendLine("      --text: #142033;")
            appendLine("      --muted: #667085;")
            appendLine("      --border: #d9e1ec;")
            appendLine("      --accent: #2563eb;")
            appendLine("      --success: #15803d;")
            appendLine("      --danger: #b42318;")
            appendLine("    }")
            appendLine("    * { box-sizing: border-box; }")
            appendLine("    body { margin: 0; font-family: Inter, Segoe UI, Arial, sans-serif; background: linear-gradient(180deg, #eef3ff 0%, var(--bg) 30%, #eef2f7 100%); color: var(--text); }")
            appendLine("    .page { max-width: 1120px; margin: 0 auto; padding: 40px 20px 56px; }")
            appendLine("    .hero { background: rgba(255,255,255,0.88); border: 1px solid var(--border); border-radius: 24px; padding: 28px; box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08); backdrop-filter: blur(8px); }")
            appendLine("    .eyebrow { text-transform: uppercase; letter-spacing: .14em; font-size: 12px; color: var(--muted); margin: 0 0 8px; }")
            appendLine("    h1 { margin: 0; font-size: clamp(30px, 4vw, 46px); line-height: 1.05; }")
            appendLine("    .subtitle { margin: 12px 0 0; color: var(--muted); font-size: 15px; line-height: 1.6; }")
            appendLine("    .grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 16px; margin-top: 18px; }")
            appendLine("    .card { background: var(--panel); border: 1px solid var(--border); border-radius: 18px; padding: 18px; }")
            appendLine("    .metric-label { font-size: 12px; text-transform: uppercase; letter-spacing: .08em; color: var(--muted); margin: 0 0 8px; }")
            appendLine("    .metric-value { font-size: 30px; font-weight: 700; margin: 0; }")
            appendLine("    .sections { display: grid; gap: 18px; margin-top: 18px; }")
            appendLine("    .section { background: rgba(255,255,255,0.92); border: 1px solid var(--border); border-radius: 24px; padding: 22px; box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04); }")
            appendLine("    .section h2 { margin: 0 0 14px; font-size: 20px; }")
            appendLine("    .tree, .tree ul { list-style: none; margin: 0; padding-left: 22px; }")
            appendLine("    .tree { padding-left: 0; }")
            appendLine("    .tree li { position: relative; margin: 10px 0; }")
            appendLine("    .tree li::before { content: ''; position: absolute; left: -14px; top: 0; bottom: -10px; width: 1px; background: var(--border); }")
            appendLine("    .tree li::after { content: ''; position: absolute; left: -14px; top: 16px; width: 14px; height: 1px; background: var(--border); }")
            appendLine("    .node { display: flex; align-items: center; gap: 10px; padding: 10px 12px; background: var(--panel); border: 1px solid var(--border); border-radius: 14px; }")
            appendLine("    .node.pass { border-color: rgba(21, 128, 61, 0.24); }")
            appendLine("    .node.fail { border-color: rgba(180, 35, 24, 0.24); }")
            appendLine("    .badge { display: inline-flex; align-items: center; border-radius: 999px; padding: 5px 10px; font-size: 12px; font-weight: 700; }")
            appendLine("    .badge.pass { background: rgba(21, 128, 61, 0.1); color: var(--success); }")
            appendLine("    .badge.fail { background: rgba(180, 35, 24, 0.1); color: var(--danger); }")
            appendLine("    .muted { color: var(--muted); }")
            appendLine("    .details { margin: 8px 0 0; padding-left: 0; }")
            appendLine("    .details li { margin: 5px 0; }")
            appendLine("    table { width: 100%; border-collapse: collapse; }")
            appendLine("    th, td { text-align: left; border-top: 1px solid var(--border); padding: 12px 10px; vertical-align: top; }")
            appendLine("    th { font-size: 12px; text-transform: uppercase; letter-spacing: .08em; color: var(--muted); }")
            appendLine("    code { background: #eef2ff; padding: 2px 6px; border-radius: 6px; }")
            appendLine("    @media (max-width: 860px) { .grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }")
            appendLine("    @media (max-width: 520px) { .grid { grid-template-columns: 1fr; } .page { padding: 18px 12px 28px; } .hero, .section { border-radius: 18px; } }")
            appendLine("  </style>")
            appendLine("</head>")
            appendLine("<body>")
            appendLine("  <div class=\"page\">")
            appendLine("    <section class=\"hero\">")
            appendLine("      <p class=\"eyebrow\">ArchGuard v0.0.1</p>")
            appendLine("      <h1>Architecture Report</h1>")
            appendLine("      <p class=\"subtitle\">Filesystem validation for <code>${escape(result.projectRoot.toString())}</code>. The report checks the configured feature root, required folders, nested features, and forbidden folder names.</p>")
            appendLine("      <div class=\"grid\">")
            metricCard("Features Found", result.allFeatures.size.toString())
            metricCard("Passed", passed.toString())
            metricCard("Failed", failed.toString())
            metricCard("Violations", totalViolations.toString())
            appendLine("      </div>")
            appendLine("    </section>")
            appendLine("")
            appendLine("    <div class=\"sections\">")
            appendLine("      <section class=\"section\">")
            appendLine("        <h2>Feature Tree</h2>")
            appendLine("        <ul class=\"tree\">")
            result.featureResults.forEach { feature ->
                renderFeature(feature, 4)
            }
            appendLine("        </ul>")
            appendLine("      </section>")
            appendLine("")
            appendLine("      <section class=\"section\">")
            appendLine("        <h2>Violations</h2>")
            if (result.violations.isEmpty()) {
                appendLine("        <p style=\"margin:0;\">No violations detected.</p>")
            } else {
                appendLine("        <table>")
                appendLine("          <thead>")
                appendLine("            <tr><th>Rule</th><th>Message</th><th>Location</th></tr>")
                appendLine("          </thead>")
                appendLine("          <tbody>")
                result.violations.forEach { violation ->
                    appendLine("            <tr>")
                    appendLine("              <td>${escape(violation.ruleId)}</td>")
                    appendLine("              <td>${escape(violation.message)}</td>")
                    appendLine("              <td>${escape(violation.location)}</td>")
                    appendLine("            </tr>")
                }
                appendLine("          </tbody>")
                appendLine("        </table>")
            }
            appendLine("      </section>")
            appendLine("    </div>")
            appendLine("  </div>")
            appendLine("</body>")
            appendLine("</html>")
        }
    }

    private fun StringBuilder.renderFeature(feature: FeatureValidation, depth: Int) {
        val indent = "  ".repeat(depth)
        val statusClass = if (feature.isPassed) "pass" else "fail"
        appendLine("${indent}<li>")
        appendLine("${indent}  <div class=\"node $statusClass\">")
        appendLine("${indent}    <span class=\"badge $statusClass\">${if (feature.isPassed) "PASS" else "FAIL"}</span>")
        appendLine("${indent}    <strong>${escape(feature.name)}</strong>")
        appendLine("${indent}    <span class=\"muted\">${escape(feature.path.toString())}</span>")
        appendLine("${indent}  </div>")

        if (feature.requiredLayers.isNotEmpty() || feature.violations.isNotEmpty() || feature.childFeatures.isNotEmpty()) {
            appendLine("${indent}  <ul>")
            feature.requiredLayers.forEach { layer ->
                appendLine("${indent}    <li>")
                appendLine("${indent}      <div class=\"node ${if (layer.present) "pass" else "fail"}\">")
                appendLine("${indent}        <span class=\"badge ${if (layer.present) "pass" else "fail"}\">${if (layer.present) "OK" else "Missing"}</span>")
                appendLine("${indent}        <strong>${escape(layer.name)}</strong>")
                appendLine("${indent}      </div>")
                appendLine("${indent}    </li>")
            }

            feature.violations.forEach { violation ->
                appendLine("${indent}    <li>")
                appendLine("${indent}      <div class=\"node fail\">")
                appendLine("${indent}        <span class=\"badge fail\">Rule</span>")
                appendLine("${indent}        <strong>${escape(violation.message)}</strong>")
                appendLine("${indent}      </div>")
                appendLine("${indent}    </li>")
            }

            feature.childFeatures.forEach { child ->
                renderFeature(child, depth + 2)
            }
            appendLine("${indent}  </ul>")
        }

        appendLine("${indent}</li>")
    }

    private fun StringBuilder.metricCard(label: String, value: String) {
        appendLine("        <div class=\"card\">")
        appendLine("          <p class=\"metric-label\">${escape(label)}</p>")
        appendLine("          <p class=\"metric-value\">${escape(value)}</p>")
        appendLine("        </div>")
    }

    private fun escape(value: String): String {
        val builder = StringBuilder(value.length)
        for (character in value) {
            when (character) {
                '&' -> builder.append("&amp;")
                '<' -> builder.append("&lt;")
                '>' -> builder.append("&gt;")
                '"' -> builder.append("&quot;")
                '\'' -> builder.append("&#39;")
                else -> builder.append(character)
            }
        }
        return builder.toString()
    }
}
