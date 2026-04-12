# ToastCompose

[![Maven Central](https://img.shields.io/maven-central/v/io.github.joyner-perez/toastcompose.svg)](https://central.sonatype.com/artifact/io.github.joyner-perez/toastcompose)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10+-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/compose-multiplatform)

A lightweight, fully customizable toast notification library for **Compose Multiplatform** (Android & iOS).

- ✅ Compose Multiplatform (Android + iOS)
- ✅ Pure Android projects
- ✅ `Scaffold` integration via `ToastHost`
- ✅ Standalone usage without `Scaffold`
- ✅ Toast queue with configurable max size
- ✅ Action button with locale-aware default label
- ✅ Swipe to dismiss
- ✅ Progress bar showing remaining display time
- ✅ Custom enter / exit animations
- ✅ Native platform toasts (Android `Toast`, iOS `UIAlertController`)
- ✅ Fully customizable: icon, color, font, shape, size
- ✅ `ToastCompose` composable — single, state-driven API to display toasts

---

## Table of Contents

1. [Requirements](#requirements)
2. [Installation](#installation)
3. [Quick Start](#quick-start)
4. [Usage with Scaffold (recommended)](#usage-with-scaffold-recommended)
5. [Usage without Scaffold](#usage-without-scaffold)
6. [Toast Types](#toast-types)
7. [Customization](#customization)
8. [Action Button](#action-button)
9. [Toast Queue](#toast-queue)
10. [Progress Bar](#progress-bar)
11. [Swipe to Dismiss](#swipe-to-dismiss)
12. [Custom Animations](#custom-animations)
13. [Native Platform Toast](#native-platform-toast)
14. [Reacting to UI State](#reacting-to-ui-state)
15. [Dismiss All](#dismiss-all)
16. [API Reference](#api-reference)
17. [License](#license)

---

## Requirements

| Tool | Version |
|---|---|
| Kotlin | 2.3+ |
| Compose Multiplatform | 1.10+ |
| Android min SDK | 24 |
| iOS | arm64 + Simulator arm64 |

---

## Installation

### Option A — Version catalog (`libs.versions.toml`) ✅ recommended

**Step 1.** Add the version and library entry to `gradle/libs.versions.toml`:

```toml
[versions]
toastCompose = "latest version"

[libraries]
toast-compose = { module = "io.github.joyner-perez:toastcompose", version.ref = "toastCompose" }
```

**Step 2.** Add the dependency in your `build.gradle.kts`:

*Compose Multiplatform project — `composeApp/build.gradle.kts`:*
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.toast.compose)
        }
    }
}
```

*Android-only project — module `build.gradle.kts`:*
```kotlin
dependencies {
    implementation(libs.toast.compose)
}
```

---

### Option B — String notation

*Compose Multiplatform project — `composeApp/build.gradle.kts`:*
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.joyner-perez:toastcompose:<latest-version>") // see badge above
        }
    }
}
```

*Android-only project — module `build.gradle.kts`:*
```kotlin
dependencies {
    implementation("io.github.joyner-perez:toastcompose:<latest-version>") // see badge above
}
```

> Gradle's variant selection automatically picks the correct artifact (AAR for Android, klib for iOS).

---

## Quick Start

`ToastCompose` is the only way to display a toast. Declare it in your composable, bind it to a state variable, and set that variable to `true` to trigger it.

```kotlin
val toastState = rememberToastState()

var showToast by remember { mutableStateOf(false) }

// 1. Declare the toast — fires whenever showToast becomes true
ToastCompose(
    toastState = toastState,
    condition = showToast,
    message = "Operation successful",
    type = ToastType.SUCCESS,
    onDismiss = { showToast = false }
)

// 2. Trigger it from any event
Button(onClick = { showToast = true }) {
    Text("Save")
}
```

---

## Usage with Scaffold (recommended)

Pass `ToastHost` to the `snackbarHost` slot of `Scaffold`. It handles positioning and insets automatically.

```kotlin
@Composable
fun MyScreen() {
    val toastState = rememberToastState()

    var showSuccess by remember { mutableStateOf(false) }
    var showError   by remember { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = showSuccess,
        message = "Operation successful",
        type = ToastType.SUCCESS,
        onDismiss = { showSuccess = false }
    )
    ToastCompose(
        toastState = toastState,
        condition = showError,
        message = "An error occurred",
        type = ToastType.ERROR,
        onDismiss = { showError = false }
    )

    Scaffold(
        snackbarHost = { ToastHost(toastState = toastState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Button(onClick = { showSuccess = true }) { Text("Show SUCCESS") }
            Button(onClick = { showError = true })   { Text("Show ERROR") }
        }
    }
}
```

---

## Usage without Scaffold

Place `ToastHost` inside a `Box` and align it manually:

```kotlin
@Composable
fun MyScreen() {
    val toastState = rememberToastState()

    var showToast by remember { mutableStateOf(false) }

    ToastCompose(
        toastState = toastState,
        condition = showToast,
        message = "File saved",
        type = ToastType.SUCCESS,
        onDismiss = { showToast = false }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { showToast = true }
        ) { Text("Save") }

        ToastHost(
            toastState = toastState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
}
```

---

## Toast Types

Four built-in types, each with a default icon and background color:

| Type | Color | Icon |
|---|---|---|
| `ToastType.SUCCESS` | Green `#2E7D32` | ✅ CheckCircle |
| `ToastType.ERROR` | Red `#C62828` | ❌ Close |
| `ToastType.INFO` | Blue `#1565C0` | ℹ️ Info |
| `ToastType.WARNING` | Orange `#E65100` | ⚠️ Warning |

```kotlin
var showSuccess by remember { mutableStateOf(false) }
var showError   by remember { mutableStateOf(false) }
var showInfo    by remember { mutableStateOf(false) }
var showWarning by remember { mutableStateOf(false) }

ToastCompose(toastState = toastState, condition = showSuccess, message = "Operation successful", type = ToastType.SUCCESS, onDismiss = { showSuccess = false })
ToastCompose(toastState = toastState, condition = showError,   message = "An error occurred",    type = ToastType.ERROR,   onDismiss = { showError = false })
ToastCompose(toastState = toastState, condition = showInfo,    message = "Important information", type = ToastType.INFO,    onDismiss = { showInfo = false })
ToastCompose(toastState = toastState, condition = showWarning, message = "Attention required",    type = ToastType.WARNING, onDismiss = { showWarning = false })
```

---

## Customization

Every visual property can be overridden per `ToastCompose` call:

```kotlin
var showCustom by remember { mutableStateOf(false) }

// Custom vector icon (Material Icons or your own ImageVector)
ToastCompose(
    toastState = toastState,
    condition = showCustom,
    message = "Custom icon, color and font",
    icon = ToastIcon.Vector(Icons.Filled.Star),
    backgroundColor = Color(0xFF6A1B9A),
    textColor = Color(0xFF00695C),
    fontFamily = FontFamily.Cursive,
    onDismiss = { showCustom = false }
)
```

```kotlin
var showDrawable by remember { mutableStateOf(false) }

// Custom drawable / painter icon
// Android-only project → use painterResource from androidx
val painter = painterResource(R.drawable.ic_my_icon)

// Compose Multiplatform → use painterResource from compose-resources
val painter = painterResource(Res.drawable.ic_my_icon)

ToastCompose(
    toastState = toastState,
    condition = showDrawable,
    message = "Toast with drawable icon",
    icon = ToastIcon.Resource(painter = painter),
    backgroundColor = Color(0xFF00695C),
    onDismiss = { showDrawable = false }
)
```

### All available parameters

```kotlin
ToastCompose(
    toastState      = toastState,
    condition       = show,               // triggers the toast when true
    message         = "Hello",
    type            = ToastType.INFO,     // controls default icon + color
    durationMillis  = 3000L,              // display time in ms (default 2500)
    icon            = ToastIcon.Vector(...), // or ToastIcon.Resource(painter)
    backgroundColor = Color.Blue,
    textColor       = Color.White,
    fontFamily      = FontFamily.Monospace,
    fontSize        = 14.sp,
    fontWeight      = FontWeight.Bold,
    iconTint        = Color.Yellow,
    iconSize        = 32.dp,
    shape           = RoundedCornerShape(8.dp),
    actionLabel     = "Undo",             // optional action button label
    onAction        = { /* on action tap */ },
    onDismiss       = { show = false }
)
```

---

## Action Button

Pass `onAction` to show a tappable action button on the trailing side. If `actionLabel` is blank, the locale-aware default **"Undo"** label is used (supports English, Spanish, French, Italian, Portuguese, German).

```kotlin
var showUndo by remember { mutableStateOf(false) }
var showView by remember { mutableStateOf(false) }

// Default label "Undo" (locale-aware)
ToastCompose(
    toastState = toastState,
    condition = showUndo,
    message = "Item deleted",
    type = ToastType.ERROR,
    onAction = { /* restore item */ },
    onDismiss = { showUndo = false }
)

// Custom label
ToastCompose(
    toastState = toastState,
    condition = showView,
    message = "File saved",
    type = ToastType.SUCCESS,
    actionLabel = "View",
    onAction = { openFile() },
    onDismiss = { showView = false }
)
```

---

## Toast Queue

If a toast is already visible when `ToastCompose` fires, the new toast is queued and shown automatically once the current one is dismissed. The queue is bounded — extra toasts beyond the limit are silently dropped.

```kotlin
// Default max queue size is 3
val toastState = rememberToastState()

// Custom max queue size
val toastState = rememberToastState(maxQueueSize = 5)

// Trigger 3 toasts at once — they queue up automatically
var showQueue by remember { mutableStateOf(false) }

ToastCompose(toastState = toastState, condition = showQueue, message = "First toast in queue",  type = ToastType.SUCCESS, onDismiss = { showQueue = false })
ToastCompose(toastState = toastState, condition = showQueue, message = "Second toast in queue", type = ToastType.INFO)
ToastCompose(toastState = toastState, condition = showQueue, message = "Third toast in queue",  type = ToastType.WARNING)
```

---

## Progress Bar

Show a thin animated bar at the bottom of the toast that depletes over `durationMillis`. Enable it once on `ToastHost` and it applies to every toast.

```kotlin
// With Scaffold
Scaffold(
    snackbarHost = {
        ToastHost(
            toastState = toastState,
            showProgressBar = true
        )
    }
) { ... }

// Without Scaffold
ToastHost(
    toastState = toastState,
    showProgressBar = true,
    modifier = Modifier.align(Alignment.BottomCenter)
)
```

---

## Swipe to Dismiss

Swipe left or right on any toast to dismiss it immediately. This behavior is built in and requires no extra setup.

---

## Custom Animations

Override the default slide-up / slide-down animations via the `enter` and `exit` parameters:

```kotlin
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

ToastHost(
    toastState = toastState,
    enter = scaleIn() + fadeIn(),
    exit  = scaleOut() + fadeOut()
)
```

```kotlin
// Horizontal slide-in from the right
ToastHost(
    toastState = toastState,
    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
    exit  = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
)
```

---

## Native Platform Toast

Use the operating system's own toast mechanism (Android `Toast`, iOS `UIAlertController`) when you don't need Compose UI:

```kotlin
@Composable
fun MyScreen() {
    val nativeToast = rememberToastNative()

    Button(onClick = {
        nativeToast.show("Short native toast")                          // SHORT by default
        nativeToast.show("Long native toast", ToastNativeDuration.LONG) // or LONG
    }) { Text("Show native toast") }
}
```

---

## Reacting to UI State

`ToastCompose` wraps `LaunchedEffect` internally — it fires the toast whenever `condition` changes to `true`, and resets automatically via `onDismiss`. This replaces the manual `LaunchedEffect` + state check pattern:

```kotlin
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val toastState = rememberToastState()

    // Fires when isLoginSuccess becomes true — no LaunchedEffect boilerplate
    ToastCompose(
        toastState = toastState,
        condition = uiState.isLoginSuccess,
        message = "Welcome back!",
        type = ToastType.SUCCESS,
        onDismiss = {
            viewModel.clearSuccess()
            navController.navigate(Route.Home)
        }
    )

    ToastCompose(
        toastState = toastState,
        condition = uiState.error.isNotBlank(),
        message = uiState.error,
        type = ToastType.ERROR,
        onDismiss = { viewModel.clearError() }
    )

    Scaffold(snackbarHost = { ToastHost(toastState = toastState) }) { ... }
}
```

`condition` acts as both the `LaunchedEffect` key and the guard — the toast shows only when it is `true` at the moment the effect fires.

---

## Dismiss All

Clear the current toast and drain the entire queue — useful when navigating away from a screen:

```kotlin
// Dismiss only the current toast (queue plays on)
toastState.dismiss()

// Dismiss current toast AND clear the queue
toastState.dismissAll()
```

---

## API Reference

### `rememberToastState`

```kotlin
@Composable
fun rememberToastState(maxQueueSize: Int = 3): ToastState
```

### `ToastCompose`

Composable that shows a toast whenever `condition` changes to `true`. No `LaunchedEffect` required.

```kotlin
@Composable
fun ToastCompose(
    toastState     : ToastState,
    condition      : Boolean        = true,
    message        : String,
    type           : ToastType      = ToastType.INFO,
    durationMillis : Long           = 2500L,
    icon           : ToastIcon      = ToastIcon.Vector(type.icon),
    backgroundColor: Color          = type.backgroundColor,
    textColor      : Color          = Color.White,
    fontFamily     : FontFamily     = FontFamily.Default,
    fontSize       : TextUnit       = TextUnit.Unspecified,
    iconTint       : Color          = Color.White,
    fontWeight     : FontWeight     = FontWeight.Medium,
    shape          : Shape          = RoundedCornerShape(12.dp),
    iconSize       : Dp             = 28.dp,
    actionLabel    : String         = "",
    onAction       : (() -> Unit)?  = null,
    onDismiss      : (() -> Unit)?  = null
)
```

### `ToastHost`

```kotlin
@Composable
fun ToastHost(
    toastState     : ToastState,
    modifier       : Modifier       = Modifier,
    showProgressBar: Boolean        = false,
    enter          : EnterTransition = slideInVertically(...) + fadeIn(),
    exit           : ExitTransition  = slideOutVertically(...) + fadeOut()
)
```

### `ToastIcon`

```kotlin
sealed class ToastIcon {
    data class Vector(val imageVector: ImageVector) : ToastIcon() // Material icon or custom
    data class Resource(val painter: Painter)       : ToastIcon() // Drawable / painter
}
```

### `ToastNative`

```kotlin
fun ToastNative.show(message: String)                                   // SHORT duration
fun ToastNative.show(message: String, duration: ToastNativeDuration)    // SHORT or LONG
```

---

## License

```
MIT License

Copyright (c) 2026 Joyner

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
