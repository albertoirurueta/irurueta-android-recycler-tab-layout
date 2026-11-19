# Implementation plan: Update dependencies

## Task summary

Bump the test-only dependency `irurueta-android-test-utils` (catalog entry `iruruetaTestUtils`) from 1.3.2 to 1.3.3, as requested by the ticket. The ticket says "specifically" this dependency but names no others, so the scope is limited to it. Other catalog versions are left untouched, and any newer versions found along the way are only reported.

Source: GitHub issue #5
Base branch: develop
Open clarifications: none

## Acceptance criteria coverage

| # | Criterion | Covered by tasks |
|---|---|---|
| 1 | "Update dependency versions, specifically from iruruetaTestUtils which has version 1.3.3 available" | Task 1 |
| 2 | derived: the library's unit tests still pass with 1.3.3 | Task 2 |
| 3 | derived: the CHANGELOG records the update, following earlier test-utils entries | Task 3 |

## Current code state

- `gradle/libs.versions.toml`: `iruruetaTestUtils = "1.3.2"` under `[versions]`. The `irurueta-test-utils` library (`com.irurueta:irurueta-android-test-utils`) references it through `version.ref`.
- `lib/build.gradle.kts:119`: the only consumer, `testImplementation(libs.irurueta.test.utils)`.
- `CHANGELOG.md`: `## [Unreleased]` has Added, Changed and Removed sections. Earlier test-utils bumps were recorded as "Test-only dependency update (`irurueta-android-test-utils` X.Y.Z); no library source changes." in the `Changed` sections.
- Project type: android-library (`lib/` plus a Compose sample `app/`), Gradle Kotlin DSL with a version catalog. No Antora page mentions the dependency.

## Implementation steps

### Group 1 — Bump and verify (Parallelizable: no — Task 2 and Task 3 depend on Task 1)

- [x] Task 1. Bump `iruruetaTestUtils` to 1.3.3 _(android)_
  - [x] Task 1.1. In `gradle/libs.versions.toml`, change `iruruetaTestUtils = "1.3.2"` to `iruruetaTestUtils = "1.3.3"`.
  - Done: `gradle/libs.versions.toml` only; no source changes, so no new tests/KDoc/coverage applicable.
- [x] Task 2. Verify the build and unit tests with the new version _(android)_
  - [x] Task 2.1. Delegate to the `iru-gate-runner` agent (`Skill iru-android-test`) to run the `lib` unit tests, and confirm they pass.
  - [x] Task 2.2. If tests fail because of 1.3.3, report the failure instead of changing library sources.
- [x] Task 3. Record the change in `CHANGELOG.md`
  - [x] Task 3.1. Under `## [Unreleased]` > `### Changed`, add: "Test-only dependency update (`irurueta-android-test-utils` 1.3.3); no library source changes."
