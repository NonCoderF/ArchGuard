# Release Checklist

Use this checklist for a public release.

## Before Release

- Update the version in the Gradle build files.
- Review the changelog.
- Run `./gradlew build`.
- Run `./gradlew validatePlugins`.
- Run `./gradlew publishPlugins --dry-run`.
- Verify the sample project with `./gradlew :sample-project:archGuardCheck`.

## Tagging

- Create a Git tag for the release version.
- Push the tag to GitHub.

## GitHub Release

- Create a GitHub Release from the tag.
- Attach release notes from `CHANGELOG.md`.

## Plugin Portal

- Set `gradle.publish.key` in `~/.gradle/gradle.properties` or through CI secrets.
- Set `gradle.publish.secret` in `~/.gradle/gradle.properties` or through CI secrets.
- Publish with `./gradlew publishPlugins`.

## Verification

- Install the plugin in a clean project with:

```kotlin
plugins {
    id("io.github.noncoderf.archguard.gradle") version "0.1.3"
}
```

- Confirm `architectureCheck` runs and produces the HTML report.
