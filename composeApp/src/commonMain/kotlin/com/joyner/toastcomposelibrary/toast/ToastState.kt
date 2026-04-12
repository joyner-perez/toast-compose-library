package com.joyner.toastcomposelibrary.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * State holder that controls toast visibility and queuing.
 *
 * Create an instance with [rememberToastState] and pass it to [ToastCompose] or [ToastHost].
 * Call [show] to enqueue a toast; it appears immediately if none is visible, or is held in
 * a bounded queue (up to [maxQueueSize]) otherwise.
 *
 * @param maxQueueSize Maximum number of pending toasts that may be queued while one is visible.
 * Additional toasts beyond this limit are silently dropped. Defaults to [DEFAULT_MAX_QUEUE_SIZE].
 */
@Stable
class ToastState(val maxQueueSize: Int = DEFAULT_MAX_QUEUE_SIZE) {
    private val scope = CoroutineScope(context = SupervisorJob() + Dispatchers.Main)
    private val queue = ArrayDeque<ToastData>()

    /**
     * The toast currently being displayed, or an empty [ToastData] when nothing is shown.
     * Observe this in your UI via [ToastCompose] or [ToastHost].
     */
    var currentToast: ToastData by mutableStateOf(value = ToastData())
        internal set

    /**
     * Shows a toast with the given [message].
     *
     * If a toast is already visible the new one is appended to the queue (up to [maxQueueSize]).
     * Once the visible toast is dismissed the next queued item is shown automatically.
     *
     * @param message Text to display inside the toast.
     * @param type Semantic type that controls the default icon and background color.
     * @param durationMillis How long (ms) the toast stays visible before auto-dismissing.
     * @param icon Icon shown on the leading side. Defaults to the vector icon for [type].
     * @param backgroundColor Fill color of the toast surface. Defaults to [type]'s color.
     * @param textColor Color of the message text.
     * @param fontFamily Typeface used for the message text.
     * @param fontSize Size of the message text. Pass [TextUnit.Unspecified] to use the default.
     * @param iconTint Tint applied to the icon.
     * @param fontWeight Weight of the message text.
     * @param shape Shape of the toast container.
     * @param iconSize Size of the icon container.
     * @param actionLabel Label for the optional action button. Falls back to the locale-aware
     * "Undo" string when blank and [onAction] is non-null.
     * @param onAction Optional callback invoked when the user taps the action button.
     * When `null` no action button is shown.
     * @param onDismiss Optional callback invoked once the toast has fully disappeared (after the
     * exit animation completes). Useful for triggering follow-up logic that should run only after
     * the toast is no longer visible. When `null` no callback is fired.
     */
    internal fun show(
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
        val toast = ToastData(
            message = message,
            type = type,
            durationMillis = durationMillis,
            customIcon = icon,
            customBackgroundColor = backgroundColor,
            customTextColor = textColor,
            customFontFamily = fontFamily,
            customFontSize = fontSize,
            customIconTint = iconTint,
            customFontWeight = fontWeight,
            customShape = shape,
            customIconSize = iconSize,
            actionLabel = actionLabel,
            onAction = onAction,
            onDismiss = onDismiss
        )
        if (isVisible()) {
            if (queue.size < maxQueueSize) {
                queue.addLast(toast)
            }
        } else {
            currentToast = toast
        }
    }

    /**
     * Dismisses the currently visible toast.
     *
     * The message is cleared immediately (triggering the exit animation), and the next queued
     * toast — if any — is shown after the animation completes.
     * Does nothing when no toast is visible.
     */
    fun dismiss() {
        if (!isVisible()) return
        val onDismiss = currentToast.onDismiss
        currentToast = currentToast.copy(message = "")
        scope.launch {
            delay(timeMillis = ExitAnimationDurationMs)
            reset()
            onDismiss?.invoke()
        }
    }

    /**
     * Dismisses the current toast and clears all pending toasts in the queue.
     *
     * Useful when navigating away from a screen and stale toasts should not appear on the
     * destination screen.
     */
    fun dismissAll() {
        queue.clear()
        dismiss()
    }

    internal fun reset() {
        currentToast = if (queue.isNotEmpty()) queue.removeFirst() else ToastData()
    }

    internal fun isVisible(): Boolean = currentToast.message.isNotBlank()

    companion object {
        /** Default maximum number of toasts that can be queued while one is visible. */
        internal const val DEFAULT_MAX_QUEUE_SIZE: Int = 3

        private const val INDEX_MESSAGE = 0
        private const val INDEX_TYPE = 1
        private const val INDEX_DURATION = 2
        private const val INDEX_BACKGROUND_COLOR = 3

        val Saver: Saver<ToastState, *> = listSaver(
            save = { state ->
                val toast = state.currentToast
                listOf(
                    toast.message,
                    toast.type.name,
                    toast.durationMillis,
                    toast.customBackgroundColor.value.toLong()
                )
            },
            restore = { list ->
                ToastState().also { state ->
                    val message = list[INDEX_MESSAGE] as String
                    if (message.isNotBlank()) {
                        val savedColor =
                            Color(value = (list[INDEX_BACKGROUND_COLOR] as Long).toULong())
                        state.show(
                            message = message,
                            type = ToastType.valueOf(list[INDEX_TYPE] as String),
                            durationMillis = list[INDEX_DURATION] as Long,
                            backgroundColor = savedColor
                        )
                    }
                }
            }
        )
    }
}

/**
 * Creates and remembers a [ToastState] that survives recompositions and configuration changes.
 *
 * @param maxQueueSize Maximum number of toasts that can be queued. See [ToastState.maxQueueSize].
 */
@Composable
fun rememberToastState(maxQueueSize: Int = ToastState.DEFAULT_MAX_QUEUE_SIZE): ToastState =
    rememberSaveable(saver = ToastState.Saver) {
        ToastState(maxQueueSize = maxQueueSize)
    }
