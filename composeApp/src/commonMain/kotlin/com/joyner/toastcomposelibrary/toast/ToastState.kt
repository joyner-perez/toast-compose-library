package com.joyner.toastcomposelibrary.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Immutable
class ToastState {
    private val scope = CoroutineScope(context = SupervisorJob() + Dispatchers.Main)

    var currentToast: ToastData by mutableStateOf(value = ToastData())
        internal set

    fun show(
        message: String,
        type: ToastType = ToastType.INFO,
        durationMillis: Long = 2500L,
        icon: ToastIcon = ToastIcon.Vector(imageVector = type.icon),
        backgroundColor: Color = type.backgroundColor
    ) {
        val toast = ToastData(
            message = message,
            type = type,
            durationMillis = durationMillis,
            customIcon = icon,
            customBackgroundColor = backgroundColor
        )
        if (isVisible()) {
            scope.launch {
                dismiss()
                delay(timeMillis = ExitAnimationDurationMs)
                currentToast = toast
            }
        } else {
            currentToast = toast
        }
    }

    fun dismiss() {
        currentToast = currentToast.copy(message = "")
    }

    internal fun reset() {
        currentToast = ToastData()
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
