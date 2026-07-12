# Changelog

## 0.1.7

Nested Gradle DSL fix release.

- Fixed `archGuard { reports { html { ... } } }`
- Fixed `archGuard { architecture { layer(...) { ... } } }`
- Added Gradle-managed nested config objects for Groovy and Kotlin DSL support
- Added functional coverage for both DSL styles

## 0.1.0

Initial public release preparation.

- Architecture DSL for feature structure validation
- Filesystem-based package validation
- HTML report generation
- Console report generation
- Gradle plugin registration for `io.github.noncoderf.archguard.gradle`
