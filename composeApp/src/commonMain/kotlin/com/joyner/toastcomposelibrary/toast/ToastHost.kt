package com.joyner.toastcomposelibrary.toast

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Convenience composable for [androidx.compose.material3.Scaffold] integration.
 *
 * Pass it to the `snackbarHost` slot so [ToastCompose] is positioned automatically
 * at the bottom of the screen, respecting the Scaffold's insets:
 * ```kotlin
 * val toastState = rememberToastState()
 * Scaffold(
 *     snackbarHost = { ToastHost(toastState = toastState) }
 * ) { innerPadding ->
 *     MyContent(modifier = Modifier.padding(innerPadding))
 * }
 * ```
 *
 * @param toastState State shared with the rest of your UI to trigger toasts.
 * @param modifier Optional [Modifier] forwarded to [ToastCompose].
 */
@Composable
fun ToastHost(toastState: ToastState, modifier: Modifier = Modifier) {
    ToastCompose(
        toastState = toastState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    )
}
