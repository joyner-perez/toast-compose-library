package com.joyner.toastcomposelibrary.toast

import androidx.compose.ui.graphics.Color

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

data class ToastData(
    val message: String = "",
    val type: ToastType = ToastType.INFO,
    val durationMillis: Long = 2500L
) {
    val isVisible: Boolean get() = message.isNotBlank()
}

internal val ToastType.backgroundColor: Color
    get() = when (this) {
        ToastType.SUCCESS -> Color(0xFF2E7D32)
        ToastType.ERROR -> Color(0xFFC62828)
        ToastType.INFO -> Color(0xFF1565C0)
        ToastType.WARNING -> Color(0xFFE65100)
    }

internal val ToastType.icon: String
    get() = when (this) {
        ToastType.SUCCESS -> "✓"
        ToastType.ERROR -> "✕"
        ToastType.INFO -> "i"
        ToastType.WARNING -> "!"
    }
