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
            appendLine("    .feature { display: grid; grid-template-columns: 180px 1fr; gap: 16px; padding: 14px 0; border-top: 1px solid var(--border); }")
            appendLine("    .feature:first-of-type { border-top: 0; padding-top: 0; }")
            appendLine("    .status { display: inline-flex; align-items: center; gap: 8px; border-radius: 999px; padding: 8px 12px; font-size: 13px; font-weight: 600; }")
            appendLine("    .status.pass { background: rgba(21, 128, 61, 0.1); color: var(--success); }")
            appendLine("    .status.fail { background: rgba(180, 35, 24, 0.1); color: var(--danger); }")
            appendLine("    .muted { color: var(--muted); }")
            appendLine("    ul { margin: 8px 0 0; padding-left: 18px; }")
            appendLine("    li { margin: 4px 0; }")
            appendLine("    table { width: 100%; border-collapse: collapse; }")
            appendLine("    th, td { text-align: left; border-top: 1px solid var(--border); padding: 12px 10px; vertical-align: top; }")
            appendLine("    th { font-size: 12px; text-transform: uppercase; letter-spacing: .08em; color: var(--muted); }")
            appendLine("    code { background: #eef2ff; padding: 2px 6px; border-radius: 6px; }")
            appendLine("    @media (max-width: 860px) { .grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } .feature { grid-template-columns: 1fr; } }")
            appendLine("    @media (max-width: 520px) { .grid { grid-template-columns: 1fr; } .page { padding: 18px 12px 28px; } .hero, .section { border-radius: 18px; } }")
            appendLine("  </style>")
            appendLine("</head>")
            appendLine("<body>")
            appendLine("  <div class=\"page\">")
            appendLine("    <section class=\"hero\">")
            appendLine("      <p class=\"eyebrow\">ArchGuard v0.0.1</p>")
            appendLine("      <h1>Architecture Report</h1>")
            appendLine("      <p class=\"subtitle\">Filesystem validation for <code>${escape(result.projectRoot.toString())}</code>. The report checks the configured feature root, required folders, and forbidden folder names.</p>")
            appendLine("      <div class=\"grid\">")
            metricCard("Features Found", result.featureResults.size.toString())
            metricCard("Passed", passed.toString())
            metricCard("Failed", failed.toString())
            metricCard("Violations", totalViolations.toString())
            appendLine("      </div>")
            appendLine("    </section>")
            appendLine("")
            appendLine("    <div class=\"sections\">")
            appendLine("      <section class=\"section\">")
            appendLine("        <h2>Feature Checks</h2>")
            result.featureResults.forEach { feature ->
                appendLine("        <div class=\"feature\">")
                appendLine("          <div>")
                appendLine("            <div class=\"status ${if (feature.isPassed) "pass" else "fail"}\">")
                appendLine("              ${if (feature.isPassed) "Passed" else "Failed"}")
                appendLine("            </div>")
                appendLine("            <p class=\"muted\" style=\"margin:10px 0 0;\">${escape(feature.name)}</p>")
                appendLine("            <p class=\"muted\" style=\"margin:4px 0 0; font-size:13px;\">${escape(feature.path.toString())}</p>")
                appendLine("          </div>")
                appendLine("          <div>")
                if (feature.missingFolders.isEmpty()) {
                    appendLine("            <p style=\"margin:0;\">All required folders are present.</p>")
                } else {
                    appendLine("            <p style=\"margin:0 0 8px;\">Missing folders</p>")
                    appendLine("            <ul>")
                    feature.missingFolders.forEach { missingFolder ->
                        appendLine("              <li>${escape(missingFolder)}</li>")
                    }
                    appendLine("            </ul>")
                }
                appendLine("          </div>")
                appendLine("        </div>")
            }
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
