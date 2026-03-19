# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ToastComposeLibrary** is a Kotlin Multiplatform project targeting Android (API 24+) and iOS (Arm64 + Simulator). It uses Compose Multiplatform for shared UI across both platforms.

## Build Commands

```bash
# Build Android debug APK
./gradlew :composeApp:assembleDebug

# Build Android release APK
./gradlew :composeApp:assembleRelease

# Run all tests
./gradlew :composeApp:allTests

# Run common unit tests
./gradlew :composeApp:testDebugUnitTest

# Run a single test class
./gradlew :composeApp:testDebugUnitTest --tests "com.joyner.toastcomposelibrary.ComposeAppCommonTest"

# Clean build
./gradlew clean
```

For iOS: open `iosApp/iosApp.xcodeproj` in Xcode and build/run from there.

## Architecture

Single module (`:composeApp`) with platform-specific source sets:

- **`commonMain`** — Shared Compose UI and business logic. All new UI and logic should go here.
- **`androidMain`** — Android entry point (`MainActivity.kt`) and Android-specific `actual` implementations.
- **`iosMain`** — iOS entry point (`MainViewController.kt`) and iOS-specific `actual` implementations.
- **`commonTest`** — Shared unit tests using `kotlin.test`.

### Expect/Actual Pattern

Platform-specific behavior is abstracted via `expect`/`actual`:
- `Platform.kt` (commonMain) defines the `expect fun getPlatform()` interface.
- `Platform.android.kt` and `Platform.ios.kt` provide platform implementations.

When adding platform-specific functionality, declare an `expect` function/class in `commonMain` and provide `actual` implementations in both `androidMain` and `iosMain`.

## Key Versions

| Component | Version |
|-----------|---------|
| Kotlin | 2.3.0 |
| Compose Multiplatform | 1.10.0 |
| AGP | 9.1.0 |
| Min SDK | 24 |
| Compile/Target SDK | 36 |

All dependency versions are managed via the version catalog at `gradle/libs.versions.toml`.
