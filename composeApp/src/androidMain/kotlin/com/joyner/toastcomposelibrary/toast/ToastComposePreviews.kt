package com.joyner.toastcomposelibrary.toast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun ToastComposeSuccessPreview() {
    val state = ToastState().also {
        it.show(message = "Operación exitosa", type = ToastType.SUCCESS)
    }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeErrorPreview() {
    val state = ToastState().also { it.show("Ocurrió un error", ToastType.ERROR) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeActionPreview() {
    val state = ToastState().also {
        it.show("Elemento eliminado", ToastType.ERROR, onAction = {})
    }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeWarningPreview() {
    val state = ToastState().also { it.show("Atención requerida", ToastType.WARNING) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeProgressBarPreview() {
    val state = ToastState().also { it.show("Con barra de progreso", ToastType.INFO) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = true, progress = { 0.6f })
    }
}
