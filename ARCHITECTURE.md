# ArchGuard Architecture Specification

> Version: 0.1.0 Status: Draft Language: Kotlin Build System: Gradle
> Kotlin DSL Java: 21 LTS

# Vision

ArchGuard is an architecture analysis platform for Kotlin and Android
projects.

Unlike style linters, ArchGuard analyzes project structure,
dependencies, layering, module boundaries, architectural health, and
long-term maintainability.

Long-term goals:

-   Gradle Plugin
-   CLI
-   IntelliJ Plugin
-   GitHub Action
-   VS Code Extension
-   HTML Dashboard
-   SARIF Reports
-   Architecture Graph Visualization

------------------------------------------------------------------------

# Repository Structure

``` text
ArchGuard
│
├── archguard-core
├── archguard-gradle-plugin
├── sample-project
├── docs
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

------------------------------------------------------------------------

# Module Responsibilities

## archguard-core

Contains all architecture analysis logic.

Packages:

``` text
engine/
parser/
model/
graph/
rules/
report/
visitor/
config/
```

Must NOT depend on Gradle.

------------------------------------------------------------------------

## archguard-gradle-plugin

Responsibilities:

-   Register architectureCheck
-   Provide Gradle DSL
-   Invoke ArchGuardEngine

No parsing logic belongs here.

------------------------------------------------------------------------

## sample-project

Small Kotlin project used for development and integration tests.

Contains intentionally good and bad architectural examples.

------------------------------------------------------------------------

# High-Level Pipeline

``` text
ArchitectureCheckTask
        │
        ▼
ArchGuardEngine
        │
        ▼
ProjectScanner
        │
        ▼
Parser
        │
        ▼
ProjectModel
        │
        ▼
DependencyGraph
        │
        ▼
RuleEngine
        │
        ▼
ReportGenerator
```

------------------------------------------------------------------------

# Project Model

The parser produces immutable models.

Examples:

-   SourceFile
-   PackageModel
-   ClassModel
-   InterfaceModel
-   ImportModel
-   FunctionModel
-   AnnotationModel
-   ModuleModel
-   DependencyEdge
-   ProjectModel
-   RuleResult
-   Violation

------------------------------------------------------------------------

# Rule API

``` kotlin
interface Rule {

    val id: String

    val title: String

    val description: String

    fun evaluate(project: ProjectModel): RuleResult
}
```

------------------------------------------------------------------------

# Planned Rules

-   Presentation must not depend on Data
-   Domain must not import Android framework
-   Repository implementation must remain internal
-   ViewModel must not own Context
-   Circular dependency detection
-   Feature dependency validation
-   Module dependency validation
-   Forbidden package imports
-   Dependency direction enforcement

------------------------------------------------------------------------

# Reports

Supported outputs:

-   Console
-   HTML
-   JSON
-   SARIF (future)

------------------------------------------------------------------------

# Gradle DSL

``` kotlin
archGuard {

    failOnViolation = true

    htmlReport = true

    jsonReport = true

    ignoredPackages = listOf(
        "generated",
        "debug"
    )

    rules {
        enable("PresentationMustNotDependOnData")
    }
}
```

------------------------------------------------------------------------

# Testing Strategy

Unit Tests

-   Parser
-   Engine
-   Rules
-   Graph

Integration Tests

-   Sample Project
-   HTML Reports

Functional Tests

Use Gradle TestKit.

Create temporary projects.

Run:

``` text
architectureCheck
```

Assert successful execution.

------------------------------------------------------------------------

# CI Pipeline

Every Pull Request

-   Build
-   Unit Tests
-   Integration Tests
-   Functional Tests

Main Branch

-   Publish Snapshot

Release

-   Publish Maven Central
-   GitHub Release

------------------------------------------------------------------------

# Coding Standards

-   Kotlin only
-   Java 21 toolchain
-   Immutable models
-   SOLID principles
-   Constructor injection
-   No Android dependencies in core
-   One responsibility per class
-   90%+ test coverage goal

------------------------------------------------------------------------

# Roadmap

## v0.1

-   Multi-module project
-   Plugin registration
-   Project scanner
-   Console report

## v0.2

-   Kotlin parser
-   Project model
-   Dependency graph

## v0.3

-   First architecture rules
-   HTML reports

## v0.5

-   JSON reports
-   Gradle DSL
-   Config file

## v1.0

-   Stable plugin
-   CLI
-   Maven Central
-   Documentation

## v2.0

-   IntelliJ plugin
-   AI architecture suggestions
-   Interactive dependency graph
-   GitHub Action
-   SARIF support

------------------------------------------------------------------------

# Definition of Done

A release is complete when:

-   Build passes
-   Tests pass
-   Plugin executes
-   Reports generated
-   Documentation updated
-   Changelog updated
-   Version tagged

------------------------------------------------------------------------

# Acceptance Criteria

Running

``` bash
./gradlew architectureCheck
```

must:

-   Scan project
-   Parse source
-   Build architecture model
-   Execute enabled rules
-   Generate report
-   Return non-zero exit code when configured to fail on violations

------------------------------------------------------------------------

# Future Vision

ArchGuard should evolve into an architecture intelligence platform
capable of:

-   Dependency visualization
-   Architecture scoring
-   Change impact analysis
-   Architectural drift detection
-   Team-wide governance
-   CI quality gates

End of Specification.
