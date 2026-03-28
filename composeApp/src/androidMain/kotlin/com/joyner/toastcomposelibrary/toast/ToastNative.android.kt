package com.joyner.toastcomposelibrary.toast

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

actual class ToastNative(private val context: Context) {
    actual fun show(message: String, duration: ToastNativeDuration) {
        val length = if (duration == ToastNativeDuration.LONG) {
            Toast.LENGTH_LONG
        } else {
            Toast.LENGTH_SHORT
        }
        Toast.makeText(context, message, length).show()
    }

    companion object {
        fun saver(context: Context): Saver<ToastNative, Boolean> = Saver(
            save = { true },
            restore = { ToastNative(context) }
        )
    }
}

@Composable
actual fun rememberToastNative(): ToastNative {
    val context = LocalContext.current
    return rememberSaveable(saver = ToastNative.saver(context)) { ToastNative(context) }
}
