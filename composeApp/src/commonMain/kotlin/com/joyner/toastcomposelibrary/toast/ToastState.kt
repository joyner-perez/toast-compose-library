package com.joyner.toastcomposelibrary.toast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Immutable
class ToastState {
    var currentToast: ToastData by mutableStateOf(value = ToastData())
        internal set

    fun show(message: String, type: ToastType = ToastType.INFO, durationMillis: Long = 2500L) {
        currentToast = ToastData(message = message, type = type, durationMillis = durationMillis)
    }

    fun dismiss() {
        currentToast = currentToast.copy(message = "")
    }

    fun reset() {
        currentToast = ToastData()
    }

    internal fun isVisible(): Boolean = currentToast.isVisible

    companion object {
        val Saver: Saver<ToastState, *> = listSaver(
            save = { state ->
                val toast = state.currentToast
                listOf(toast.message, toast.type.name, toast.durationMillis)
            },
            restore = { list ->
                ToastState().also { state ->
                    val message = list[0] as String
                    if (message.isNotBlank()) {
                        state.show(
                            message = message,
                            type = ToastType.valueOf(list[1] as String),
                            durationMillis = list[2] as Long
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun rememberToastState(): ToastState = rememberSaveable(saver = ToastState.Saver) { ToastState() }
