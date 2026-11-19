# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.12] - 2026-11-19

### Added

- Antora documentation site with code examples that are compiled and tested alongside the library.
- New GitHub Actions workflows for CI, release, `main`/`develop` version sync and security scanning, plus
  Dependabot for dependency updates.

### Changed

- Repository moved to a gitflow branching model with a `develop` integration branch; development version is now
  `1.2.0-SNAPSHOT`.
- Build toolchain upgraded to Gradle 9.9.1, Android Gradle Plugin 9.5.1 and Kotlin 2.4.21.
- Test-only dependency update (`irurueta-android-test-utils` 1.3.3); no library source changes.

### Removed

- Legacy `main.yml` and `publish.yml` workflows, superseded by the new CI and release workflows.

## [1.1.11] - 2025-12-27

### Changed

- Minimum supported SDK raised from 24 to 26.
- Dependency updates: Android Gradle Plugin 8.13.2, Kotlin 2.3.0, Compose BOM 2025.12.01, Dokka 2.1.0,
  Maven publish plugin 0.35.0.

## [1.1.10] - 2025-09-23

### Changed

- Material Components dependency updated to 1.13.0.

## [1.1.9] - 2025-09-21

### Changed

- Dependency updates: Android Gradle Plugin 8.13.0, Kotlin 2.2.20, Compose BOM 2025.09.00, AndroidX test libraries
  and Robolectric 4.16.

## [1.1.8] - 2025-07-28

### Changed

- Test-only dependency update (`irurueta-android-test-utils` 1.3.0); no library source changes.

## [1.1.7] - 2025-07-27

### Changed

- Test-only dependency update (`irurueta-android-test-utils` 1.2.0); no library source changes.

## [1.1.6] - 2025-07-27

### Changed

- Test-only dependency update (`irurueta-android-test-utils` 1.1.0); no library source changes.

## [1.1.5] - 2025-07-27

### Changed

- Unit tests now use the shared `irurueta-android-test-utils` library instead of an in-repository test helper.
- Dependency updates: MockK 1.14.5, Maven publish plugin 0.34.0.

## [1.1.4] - 2025-07-17

### Changed

- Compose BOM updated to 2025.07.00 (sample app only); no library source changes.

## [1.1.3] - 2025-07-14

### Changed

- Dependency and plugin updates: Gradle 8.14.3, Android Gradle Plugin 8.11.1, Kotlin 2.2.0.

## [1.1.2] - 2025-06-15

### Changed

- Gradle wrapper updated to 8.14.2; build tooling only.

## [1.1.0] - 2025-06-09

### Changed

- Compile SDK raised to 36 and minimum SDK raised from 21 to 24.
- Build migrated to Gradle Kotlin DSL with a version catalog, on Gradle 8.14.1 and Android Gradle Plugin 8.10.1.
- `onDraw` now takes a non-null `Canvas`, matching the current Android SDK signature.
- Style attributes are no longer read in layout editor (edit) mode.
- Sample app upgraded to a current Compose setup.

## [1.0.1] - 2023-03-05

### Changed

- Compile SDK raised to 33 and Gradle wrapper updated to 7.5.
- Apache 2.0 license header added to library sources.

## [1.0.0] - 2021-10-16

### Added

- `RecyclerTabLayout`, a `RecyclerView`-based alternative to Material `TabLayout` for many or memory-heavy tabs
  (e.g. image thumbnails), with a TabLayout-style line indicator.
- Styleable attributes: `tabIndicatorColor`, `tabIndicatorHeight`, `tabIndicatorMarginStart`,
  `tabIndicatorMarginEnd`, `tabOnScreenLimitEnabled`, `tabMinWidth` and `scrollEnabled`, with RTL layout support.
- Sample app demonstrating the view.

[Unreleased]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.12...HEAD
[1.1.12]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.11...1.1.12
[1.1.11]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.10...1.1.11
[1.1.10]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.9...1.1.10
[1.1.9]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.8...1.1.9
[1.1.8]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.7...1.1.8
[1.1.7]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.6...1.1.7
[1.1.6]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.5...1.1.6
[1.1.5]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.4...1.1.5
[1.1.4]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.3...1.1.4
[1.1.3]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.2...1.1.3
[1.1.2]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.1.0...1.1.2
[1.1.0]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.0.1...1.1.0
[1.0.1]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/compare/1.0.0...1.0.1
[1.0.0]: https://github.com/albertoirurueta/irurueta-android-recycler-tab-layout/releases/tag/1.0.0
