package com.joyner.toastcomposelibrary.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * Eliminates the need to wrap [ToastState.show] in a [LaunchedEffect] manually:
 * ```kotlin
 * // Before
 * LaunchedEffect(uiState.isSuccess) {
 *     if (uiState.isSuccess) {
 *         toastState.show(message = "Done!", type = ToastType.SUCCESS, onDismiss = { ... })
 *     }
 * }
 *
 * // After
 * toastState.ShowEffect(
 *     uiState.isSuccess,
 *     condition = uiState.isSuccess,
 *     message = "Done!",
 *     type = ToastType.SUCCESS,
 *     onDismiss = { ... }
 * )
 * ```
 *
 * @param condition When `true` at the moment the effect fires, the toast is shown.
 * Defaults to `true` so you can pass a single truthy key and skip the redundant condition.
 * @param message Text to display inside the toast.
 * @param type Semantic type controlling the default icon and background color.
 * @param durationMillis How long (ms) the toast stays visible before auto-dismissing.
 * @param icon Icon shown on the leading side.
 * @param backgroundColor Fill color of the toast surface.
 * @param textColor Color of the message text.
 * @param fontFamily Typeface used for the message text.
 * @param fontSize Size of the message text. Pass [TextUnit.Unspecified] to use the default.
 * @param iconTint Tint applied to the icon.
 * @param fontWeight Weight of the message text.
 * @param shape Shape of the toast container.
 * @param iconSize Size of the icon container.
 * @param actionLabel Label for the optional action button.
 * @param onAction Callback invoked when the user taps the action button. `null` hides the button.
 * @param onDismiss Callback invoked once the toast has fully disappeared.
 */
@Composable
fun ToastState.ShowToast(
    condition: Boolean = true,
    message: String,
    type: ToastType = ToastType.INFO,
    durationMillis: Long = 2500L,
    icon: ToastIcon = ToastIcon.Vector(imageVector = type.icon),
    backgroundColor: Color = type.backgroundColor,
    textColor: Color = Color.White,
    fontFamily: FontFamily = FontFamily.Default,
    fontSize: TextUnit = TextUnit.Unspecified,
    iconTint: Color = Color.White,
    fontWeight: FontWeight = FontWeight.Medium,
    shape: Shape = RoundedCornerShape(size = 12.dp),
    iconSize: Dp = DefaultIconSize,
    actionLabel: String = "",
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    LaunchedEffect(condition) {
        if (condition) {
            show(
                message = message,
                type = type,
                durationMillis = durationMillis,
                icon = icon,
                backgroundColor = backgroundColor,
                textColor = textColor,
                fontFamily = fontFamily,
                fontSize = fontSize,
                iconTint = iconTint,
                fontWeight = fontWeight,
                shape = shape,
                iconSize = iconSize,
                actionLabel = actionLabel,
                onAction = onAction,
                onDismiss = onDismiss
            )
        }
    }
}
