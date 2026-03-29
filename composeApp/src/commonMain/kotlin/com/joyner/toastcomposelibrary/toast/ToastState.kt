package com.joyner.toastcomposelibrary.toast

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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

@Immutable
class ToastState {
    private val scope = CoroutineScope(context = SupervisorJob() + Dispatchers.Main)
    private val queue = ArrayDeque<ToastData>()

    var currentToast: ToastData by mutableStateOf(value = ToastData())
        internal set

    fun show(
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
        onAction: (() -> Unit)? = null
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
            onAction = onAction
        )
        if (isVisible()) {
            queue.addLast(toast)
        } else {
            currentToast = toast
        }
    }

    fun dismiss() {
        if (!isVisible()) return
        currentToast = currentToast.copy(message = "")
        scope.launch {
            delay(timeMillis = ExitAnimationDurationMs)
            reset()
        }
    }

    internal fun reset() {
        currentToast = if (queue.isNotEmpty()) queue.removeFirst() else ToastData()
    }

    internal fun isVisible(): Boolean = currentToast.message.isNotBlank()

    companion object {
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

@Composable
fun rememberToastState(): ToastState = rememberSaveable(saver = ToastState.Saver) { ToastState() }
