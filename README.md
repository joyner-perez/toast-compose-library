# ToastCompose

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
14. [Dismiss All](#dismiss-all)
15. [API Reference](#api-reference)
16. [License](#license)

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

### Compose Multiplatform project

In your `composeApp/build.gradle.kts`, add the dependency to `commonMain`:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.joyner-perez:toastcompose:0.0.1")
        }
    }
}
```

### Android-only project

In your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.joyner-perez:toastcompose:0.0.1")
}
```

> Gradle's variant selection automatically picks the correct artifact (AAR for Android, klib for iOS).

---

## Quick Start

```kotlin
// 1. Create the state — survives recomposition and configuration changes
val toastState = rememberToastState()

// 2. Show a toast anywhere in your screen logic
toastState.show("Operation successful", ToastType.SUCCESS)
```

---

## Usage with Scaffold (recommended)

Pass `ToastHost` to the `snackbarHost` slot of `Scaffold`. It handles positioning and insets automatically.

```kotlin
@Composable
fun MyScreen() {
    val toastState = rememberToastState()

    Scaffold(
        snackbarHost = { ToastHost(toastState = toastState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            Button(onClick = {
                toastState.show("Operation successful", ToastType.SUCCESS)
            }) { Text("Show SUCCESS") }

            Button(onClick = {
                toastState.show("An error occurred", ToastType.ERROR)
            }) { Text("Show ERROR") }

            Button(onClick = {
                toastState.show("Important information", ToastType.INFO)
            }) { Text("Show INFO") }

            Button(onClick = {
                toastState.show("Attention required", ToastType.WARNING)
            }) { Text("Show WARNING") }
        }
    }
}
```

---

## Usage without Scaffold

Place `ToastCompose` inside a `Box` and align it manually:

```kotlin
@Composable
fun MyScreen() {
    val toastState = rememberToastState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Your screen content
        Button(
            modifier = Modifier.align(Alignment.Center),
            onClick = { toastState.show("File saved", ToastType.SUCCESS) }
        ) { Text("Save") }

        // Toast overlay — pinned to the bottom center
        ToastCompose(
            toastState = toastState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 24.dp)
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
toastState.show("Operation successful", ToastType.SUCCESS)
toastState.show("An error occurred",    ToastType.ERROR)
toastState.show("Important information", ToastType.INFO)
toastState.show("Attention required",   ToastType.WARNING)
```

---

## Customization

Every visual property can be overridden per toast call:

```kotlin
// Custom vector icon (Material Icons or your own ImageVector)
toastState.show(
    message = "Custom icon, color and font",
    icon = ToastIcon.Vector(Icons.Filled.Star),
    backgroundColor = Color(0xFF6A1B9A),
    textColor = Color(0xFF00695C),
    fontFamily = FontFamily.Cursive
)
```

```kotlin
// Custom drawable / painter icon
// Android-only project → use painterResource from androidx
val painter = painterResource(R.drawable.ic_my_icon)

// Compose Multiplatform → use painterResource from compose-resources
val painter = painterResource(Res.drawable.ic_my_icon)

toastState.show(
    message = "Toast with drawable icon",
    icon = ToastIcon.Resource(painter = painter),
    backgroundColor = Color(0xFF00695C)
)
```

### All available parameters

```kotlin
toastState.show(
    message         = "Hello",
    type            = ToastType.INFO,        // controls default icon + color
    durationMillis  = 3000L,                 // display time in ms (default 2500)
    icon            = ToastIcon.Vector(...), // or ToastIcon.Resource(painter)
    backgroundColor = Color.Blue,
    textColor       = Color.White,
    fontFamily      = FontFamily.Monospace,
    fontSize        = 14.sp,
    fontWeight      = FontWeight.Bold,
    iconTint        = Color.Yellow,
    iconSize        = 32.dp,
    shape           = RoundedCornerShape(8.dp),
    actionLabel     = "Undo",                // optional action button label
    onAction        = { /* on action tap */ }
)
```

---

## Action Button

Pass `onAction` to show a tappable action button on the trailing side. If `actionLabel` is blank, the locale-aware default **"Undo"** label is used (supports English, Spanish, French, Italian, Portuguese, German).

```kotlin
// Default label "Undo" (locale-aware)
toastState.show(
    message  = "Item deleted",
    type     = ToastType.ERROR,
    onAction = { /* restore item */ }
)

// Custom label
toastState.show(
    message     = "File saved",
    type        = ToastType.SUCCESS,
    actionLabel = "View",
    onAction    = { openFile() }
)
```

---

## Toast Queue

If a toast is already visible when `show()` is called, the new toast is queued and shown automatically once the current one is dismissed. The queue is bounded — extra toasts beyond the limit are silently dropped.

```kotlin
// Default max queue size is 3
val toastState = rememberToastState()

// Custom max queue size
val toastState = rememberToastState(maxQueueSize = 5)

// Enqueue multiple toasts at once
toastState.show("First toast in queue",  ToastType.SUCCESS)
toastState.show("Second toast in queue", ToastType.INFO)
toastState.show("Third toast in queue",  ToastType.WARNING)
```

---

## Progress Bar

Show a thin animated bar at the bottom of the toast that depletes over `durationMillis`. Enable it once on `ToastHost` or `ToastCompose` and it applies to every toast.

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
ToastCompose(
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

### `ToastState.show`

```kotlin
fun show(
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
    onAction       : (() -> Unit)?  = null
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

### `ToastCompose`

```kotlin
@Composable
fun ToastCompose(
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
