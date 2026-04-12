package com.joyner.toastcomposelibrary.toast

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
 * @param showProgressBar When `true`, a thin progress bar is shown indicating remaining time.
 * @param enter Transition used when the toast becomes visible.
 * @param exit Transition used when the toast is dismissed.
 */
@Composable
fun ToastHost(
    toastState: ToastState,
    modifier: Modifier = Modifier,
    showProgressBar: Boolean = false,
    enter: EnterTransition = slideInVertically(initialOffsetY = { it }) + fadeIn(),
    exit: ExitTransition = slideOutVertically(targetOffsetY = { it }) + fadeOut()
) {
    ToastRenderer(
        toastState = toastState,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        showProgressBar = showProgressBar,
        enter = enter,
        exit = exit
    )
}
