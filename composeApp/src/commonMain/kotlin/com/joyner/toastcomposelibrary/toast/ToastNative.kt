package com.joyner.toastcomposelibrary.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

enum class ToastNativeDuration { SHORT, LONG }

@Stable
expect class ToastNative {
    fun show(message: String, duration: ToastNativeDuration)
}

fun ToastNative.show(message: String) = show(message, ToastNativeDuration.SHORT)

@Composable
expect fun rememberToastNative(): ToastNative
