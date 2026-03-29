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
        it.show(message = "Operation successful", type = ToastType.SUCCESS)
    }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeErrorPreview() {
    val state = ToastState().also { it.show("An error occurred", ToastType.ERROR) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeActionPreview() {
    val state = ToastState().also {
        it.show("Item deleted", ToastType.ERROR, onAction = {})
    }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeWarningPreview() {
    val state = ToastState().also { it.show("Attention required", ToastType.WARNING) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = false, progress = { 1f })
    }
}

@Preview
@Composable
fun ToastComposeProgressBarPreview() {
    val state = ToastState().also { it.show("With progress bar", ToastType.INFO) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast, onDismiss = {
        }, showProgressBar = true, progress = { 0.6f })
    }
}
