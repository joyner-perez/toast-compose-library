package com.joyner.toastcomposelibrary.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class ToastState {
    var currentToast: ToastData? by mutableStateOf(null)
        internal set

    fun show(
        message: String,
        type: ToastType = ToastType.INFO,
        durationMillis: Long = 2500L
    ) {
        currentToast = ToastData(
            message = message,
            type = type,
            durationMillis = durationMillis
        )
    }

    fun dismiss() {
        currentToast = null
    }
}

@Composable
fun rememberToastState(): ToastState = remember { ToastState() }
