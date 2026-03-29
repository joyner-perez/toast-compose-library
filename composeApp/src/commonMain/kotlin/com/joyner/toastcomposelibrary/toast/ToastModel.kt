package com.joyner.toastcomposelibrary.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

internal const val ExitAnimationDurationMs = 300L
internal val DefaultIconSize: Dp = 28.dp

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

/**
 * Immutable snapshot of all properties needed to render a single toast notification.
 *
 * You typically don't construct this directly — use [ToastState.show] instead, which builds it
 * from individual parameters with sensible defaults.
 *
 * @property message Text displayed inside the toast.
 * @property type Semantic type that controls the default icon and background color.
 * @property durationMillis How long (ms) the toast stays visible before auto-dismissing.
 * @property customIcon Icon shown on the leading side of the toast.
 * @property customBackgroundColor Fill color of the toast surface.
 * @property customTextColor Color of the [message] text.
 * @property customFontFamily Typeface used for the [message] text.
 * @property customFontSize Size of the [message] text; [TextUnit.Unspecified] uses the default.
 * @property customIconTint Tint applied to [customIcon].
 * @property customFontWeight Weight of the [message] text.
 * @property customShape Shape of the toast container.
 * @property customIconSize Size of the icon container.
 * @property actionLabel Label for the optional action button. Falls back to the locale-aware
 * "Undo" string when blank and [onAction] is non-null.
 * @property onAction Callback invoked when the user taps the action button. `null` hides the button.
 */
data class ToastData(
    val message: String = "",
    val type: ToastType = ToastType.INFO,
    val durationMillis: Long = 2500L,
    val customIcon: ToastIcon = ToastIcon.Vector(imageVector = type.icon),
    val customBackgroundColor: Color = type.backgroundColor,
    val customTextColor: Color = Color.White,
    val customFontFamily: FontFamily = FontFamily.Default,
    val customFontSize: TextUnit = TextUnit.Unspecified,
    val customIconTint: Color = Color.White,
    val customFontWeight: FontWeight = FontWeight.Medium,
    val customShape: Shape = RoundedCornerShape(size = 12.dp),
    val customIconSize: Dp = DefaultIconSize,
    val actionLabel: String = "",
    val onAction: (() -> Unit)? = null
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
