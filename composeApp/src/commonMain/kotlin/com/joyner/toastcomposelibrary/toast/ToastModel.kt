package com.joyner.toastcomposelibrary.toast

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

internal const val ExitAnimationDurationMs = 300L

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO,
    WARNING
}

sealed class ToastIcon {
    data class Vector(val imageVector: ImageVector) : ToastIcon()
    data class Resource(val painter: Painter) : ToastIcon()
}

data class ToastData(
    val message: String = "",
    val type: ToastType = ToastType.INFO,
    val durationMillis: Long = 2500L,
    val customIcon: ToastIcon = ToastIcon.Vector(imageVector = type.icon),
    val customBackgroundColor: Color = type.backgroundColor
)

internal val ToastType.backgroundColor: Color
    get() = when (this) {
        ToastType.SUCCESS -> Color(0xFF2E7D32)
        ToastType.ERROR -> Color(0xFFC62828)
        ToastType.INFO -> Color(0xFF1565C0)
        ToastType.WARNING -> Color(0xFFE65100)
    }

internal val ToastType.icon: ImageVector
    get() = when (this) {
        ToastType.SUCCESS -> Icons.Filled.CheckCircle
        ToastType.ERROR -> Icons.Filled.Close
        ToastType.INFO -> Icons.Filled.Info
        ToastType.WARNING -> Icons.Filled.Warning
    }
