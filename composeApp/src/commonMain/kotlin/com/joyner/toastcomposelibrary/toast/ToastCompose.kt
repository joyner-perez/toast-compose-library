package com.joyner.toastcomposelibrary.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val ExitAnimationDurationMs = 300L

/**
 * Standalone toast composable managed by [toastState].
 *
 * Place it anywhere in your layout — typically at the bottom of a [Box] or inside a [Scaffold].
 * Visibility is handled internally: the toast appears when [ToastState.show] is called and
 * auto-dismisses after [ToastData.durationMillis].
 *
 * Example usage:
 * ```kotlin
 * val toastState = rememberToastState()
 *
 * Box(Modifier.fillMaxSize()) {
 *     MyScreenContent(
 *         onSuccess = { toastState.show("Guardado", ToastType.SUCCESS) },
 *         onError   = { toastState.show("Error", ToastType.ERROR) }
 *     )
 *     ToastCompose(
 *         toastState = toastState,
 *         modifier = Modifier
 *             .align(Alignment.BottomCenter)
 *             .navigationBarsPadding()
 *             .padding(horizontal = 16.dp, vertical = 24.dp)
 *     )
 * }
 * ```
 *
 * @param toastState State that controls what message is shown and when to dismiss.
 * Use [rememberToastState] to create and [rememberSaveable] an instance.
 * @param modifier Optional [Modifier] to control size and position of the toast.
 */
@Composable
fun ToastCompose(toastState: ToastState, modifier: Modifier = Modifier) {
    val toast = toastState.currentToast

    AnimatedVisibility(
        visible = toastState.isVisible(),
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        ToastItem(toast = toast)
    }

    LaunchedEffect(toastState.isVisible()) {
        if (toastState.isVisible()) {
            delay(timeMillis = toast.durationMillis)
            toastState.dismiss()
            delay(timeMillis = ExitAnimationDurationMs)
            toastState.reset()
        }
    }
}

@Composable
private fun ToastItem(toast: ToastData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(size = 12.dp))
            .background(toast.type.backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = toast.type.icon,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = toast.message,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun ToastComposeSuccessPreview() {
    val state = ToastState().also { it.show("Operación exitosa", ToastType.SUCCESS) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast)
    }
}

@Preview
@Composable
private fun ToastComposeErrorPreview() {
    val state = ToastState().also { it.show("Ocurrió un error", ToastType.ERROR) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast)
    }
}

@Preview
@Composable
private fun ToastComposeInfoPreview() {
    val state = ToastState().also { it.show("Información importante", ToastType.INFO) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast)
    }
}

@Preview
@Composable
private fun ToastComposeWarningPreview() {
    val state = ToastState().also { it.show("Atención requerida", ToastType.WARNING) }
    Box(modifier = Modifier.padding(16.dp)) {
        ToastItem(toast = state.currentToast)
    }
}

// endregion
