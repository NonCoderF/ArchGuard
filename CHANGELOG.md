# Changelog

## 0.1.8

Groovy DSL compatibility release.

- Added Closure-based delegates for `reports`, `html`, `architecture`, and `layer`
- Fixed Groovy builds resolving `layer()` on the root `archGuard` extension
- Kept Kotlin DSL support through `Action` overloads

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
