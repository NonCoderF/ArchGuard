# Contributing to ArchGuard

Thanks for helping improve ArchGuard.

## Build

- Use Java 21.
- Run `./gradlew build` before opening a pull request.

## Tests

- Core unit tests live in `archguard-plugin`.
- Plugin functional tests live in `archguard-plugin`.
- The sample project provides a smoke test for the installed plugin workflow.

## Code Style

- Kotlin only.
- Prefer immutable models.
- Keep classes small and single-purpose.
- Avoid introducing dependencies into `archguard-plugin` unless they are required for analysis or reporting.

## Pull Requests

- Keep changes focused.
- Include tests for behavior changes.
- Update the README or release notes when user-facing behavior changes.
